//
//  CRNViewController.m
//  CRNDemo
//
//  Created by CRN on 2019/3/5.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "CRNDefine.h"

#import "CRNViewController.h"
#import "CRNView.h"
#import <React/RCTDefines.h>
#import <React/RCTBridge+Private.h>
#import <React/RCTEventDispatcher.h>
#import <React/UIView+React.h>
#import "CRNBridgeManager.h"
#import "CRNPlugin.h"
#import "CRNUnbundlePackage.h"

@interface CRNViewController ()<CRNViewDelegate,CRNViewLoadingDelegate>
@property (nonatomic, strong) CRNURL *url;
@property (nonatomic, strong) CRNView *rctView;

@property (nonatomic, strong) NSDictionary *initialProperties;

@property (nonatomic, readonly) RCTBridge *rctBridge;


@end

@implementation CRNViewController

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    NSDictionary *tmpDict = [self.rctBridge.pluginObjectsDict copy];
    for (CRNPlugin *plugIn in tmpDict) {
        if ([plugIn isKindOfClass:[CRNPlugin class]]) {
            [plugIn clear];
        }
    }
    @synchronized(self.rctBridge.pluginObjectsDict) {
        [self.rctBridge.pluginObjectsDict removeAllObjects];
    }
    self.rctBridge.crnView = nil;

}

- (instancetype)initWithURL:(CRNURL *)url_
{
    return [self initWithURL:url_ andInitialProperties:nil];
}

- (instancetype)initWithURL:(CRNURL *)url andInitialProperties:(NSDictionary *)initialProperties
{
    if (self = [super init]) {
        self.url = url;
        self.initialProperties = initialProperties;
    }
    
    return self;
}

-(CRNURL *)crnURL
{
    return self.url;
}

- (RCTBridge *)rctBridge {
    return self.rctView.bridge;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    self.rctView = [[CRNView alloc] initWithURL:self.url
                                          frame:self.view.bounds
                              initialProperties:self.initialProperties
                                  launchOptions:nil];
    self.rctView.frame = self.view.bounds;
    self.rctView.loadingDelegate = self;
    self.rctView.viewDelegate = self;
    [self.view addSubview:self.rctView];
    
    self.view.backgroundColor = [UIColor whiteColor];
    
    [self.rctView loadCRNViewWithURL:self.crnURL];

}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

#pragma mark ---- CRNViewDelegate
- (void)crnViewLoadFailed:(CRNView *)view errorCode:(NSNumber *)code {
    
}

- (void)crnViewWillAppear:(CRNView *)view {
    
}

#pragma mark ---- CRNViewLoadingDelegate
- (void)showLoadingView {

}

- (void)hideLoadingView {

}

- (void)showLoadFailViewWithCode:(NSNumber *)code {
    
}
@end

