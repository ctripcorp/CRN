//
//  CRNView.m
//  CRNDemo
//
//  Created by CRN on 2019/3/5.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "CRNView.h"
#import <React/RCTBridgeDelegate.h>
#import "CRNBridgeManager.h"
#import <React/RCTBridge+Private.h>
#import <React/RCTEventDispatcher.h>
#import "CRNUnbundlePackage.h"
#import <React/RCTExceptionsManager.h>
#import <React/RCTRootView.h>

@interface CRNView () {
    double createViewTime;
}
@property (nonatomic, strong) RCTRootView *reactView;
@property (nonatomic, strong) CRNURL *url;
@property (nonatomic, strong) RCTBridge *currentBridge;
@property (nonatomic, strong) NSDictionary *initialProps;
@property (nonatomic, strong) NSDictionary *launchOptions;

@end

@implementation CRNView

-(void)dealloc {
    if (self.currentBridge != NULL) {
        
        [[NSNotificationCenter defaultCenter] postNotificationName:kCRNViewDidReleasedNotification
                                                            object:nil
                                                          userInfo:@{@"bridge":self.currentBridge}];
        
        NSDictionary *tmpDict = [self.currentBridge.pluginObjectsDict copy];
        for (CRNPlugin *plugIn in tmpDict) {
            if ([plugIn isKindOfClass:[CRNPlugin class]]) {
                [plugIn clear];
            }
        }
        @synchronized(self.currentBridge.pluginObjectsDict) {
            [self.currentBridge.pluginObjectsDict removeAllObjects];
        }
        self.currentBridge.crnView = nil;
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (id)initWithURL:(CRNURL *)rnURL
            frame:(CGRect)frame
initialProperties:(NSDictionary *)props
    launchOptions:(NSDictionary *)options {
    if (self = [super initWithFrame:frame]) {
        self.url = rnURL;
        self.initialProps = props;
        self.launchOptions = options;
        createViewTime = [[NSDate date] timeIntervalSince1970];
    }
    return self;
}

- (RCTBridge *)bridge {
    return self.currentBridge;
}

- (RCTRootView *)reactRootView{
    return _reactView;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor{
    super.backgroundColor = backgroundColor;
    self.reactView.backgroundColor = backgroundColor;
}


#pragma mark - ---- Bridge create

- (RCTBridge *)makeCurrentBridge {
    [self.url readUnbundleFilePathIfNeed];
    RCTBridge *bridge = [[CRNBridgeManager sharedCRNBridgeManager] bridgeForURL:self.url
                                                                 viewCreateTime:createViewTime
                                                                 moduleProvider:nil
                                                                   launchOption:self.launchOptions];
    
    bridge.enterViewTime = createViewTime;
    bridge.isRenderSuccess = NO;
    
    bridge.crnView = self;
    self.currentBridge = bridge;
    [self addNotificationsForCurrentBridge];
    return bridge;
}

- (void)addNotificationsForCurrentBridge {
    if (self.currentBridge) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kCRNViewDidCreateNotification
                                                            object:nil
                                                          userInfo:@{@"bridge":self.currentBridge}];
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(crnViewLoadFailedNotification:)
                                                 name:CRNViewLoadFailedNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(crnViewDidRenderNotifation:)
                                                 name:CRNViewDidRenderSuccess
                                               object:nil];
    
}



#pragma mark - --- load API

- (void)loadCRNViewWithURL:(CRNURL *)url_ {
    if (url_ == nil) {
        return;
    }
    
    self.url = url_;

    [self loadCRNViewWhenWorkDirExist];
    
}

- (void)loadCRNViewWhenWorkDirExist {    
    [self makeCurrentBridge];
    NSMutableDictionary *props = [NSMutableDictionary dictionary];
    if (self.initialProps) {
        [props addEntriesFromDictionary:self.initialProps];
    }
    [props setValue:self.url.rnBundleURL.absoluteString forKey:@"url"];
    
    NSString *moduleName = self.url.rnModuleName;
    if (self.url.isUnbundleRNURL) {
        moduleName = kDefaultCRNUnbundleMainModuleName;
    }
    self.reactView = [[RCTRootView alloc] initWithBridge:self.currentBridge
                                              moduleName:moduleName
                                       initialProperties:props];
    if (self.backgroundColor) {
        self.reactView.backgroundColor = self.backgroundColor;
    }
    self.reactView.frame = self.bounds;
    self.reactView.hidden = NO;
    [self addSubview:self.reactView];
    self.reactView.contentMode = UIViewContentModeCenter;
    self.reactView.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
    self.currentBridge = self.reactView.bridge;
    eCRNBridgeState originalBridgeState = self.reactView.bridge.originalBridgeState;
    BOOL isCacheBridge =  (originalBridgeState == Bridge_State_Dirty) || (originalBridgeState == Bridge_State_Ready);
    if (!isCacheBridge) {
        [self showLoadingView];
    }
}


#pragma mark - ----- loading

- (void)showLoadingView {
    if ([self.loadingDelegate respondsToSelector:@selector(showLoadingView)]) {
        [self.loadingDelegate showLoadingView];
    }
}

- (void)hideLoadingView {
    if ([self.loadingDelegate respondsToSelector:@selector(hideLoadingView)]) {
        [self.loadingDelegate hideLoadingView];
    }
}

- (void)showLoadFailViewWithCode:(NSNumber *)code {
    if ([self.loadingDelegate respondsToSelector:@selector(showLoadFailViewWithCode:)]) {
        [self.loadingDelegate showLoadFailViewWithCode:code];
    }
}

#pragma mark - Notification handle

- (void)crnViewLoadFailedNotification:(NSNotification *)notification {
    if (self.viewDelegate && [self.viewDelegate respondsToSelector:@selector(crnViewLoadFailed:errorCode:)]) {
        dispatch_main_sync(^{
            RCTBridge *tmpBridge = [notification.userInfo valueForKey:@"bridge"];
            int errorCode = [[notification.userInfo valueForKey:@"errorCode"] intValue];
            if ([tmpBridge isEqual:self.currentBridge]) {
                tmpBridge.bridgeState = Bridge_State_Error;
                [self.viewDelegate crnViewLoadFailed:self errorCode:@(errorCode)];
            }
        });
    }
}

- (void)crnViewDidRenderNotifation:(NSNotification *)notification {
    if (self.viewDelegate && [self.viewDelegate respondsToSelector:@selector(crnViewWillAppear:)]) {
        dispatch_main_sync(^{
            RCTBridge *tmpBridge = [notification.userInfo valueForKey:@"bridge"];
            if ([tmpBridge.crnView isEqual:self]) {
                [self.viewDelegate crnViewWillAppear:self];
            }
        });
    }
}

@end

