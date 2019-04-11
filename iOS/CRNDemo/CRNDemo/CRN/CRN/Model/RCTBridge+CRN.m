//
//  RCTBridge+CRN.m
//  CTBusiness
//
//  Created by CRN on 16/7/21.
//  Copyright © 2016年 Ctrip. All rights reserved.
//

#import "RCTBridge+CRN.h"
#import <objc/runtime.h>
#import "CRNView.h"


@implementation RCTBridge(CRN)

+ (NSString *)keyFromURL:(NSURL *)url {
    if (url == NULL) {
        return NULL;
    }
    
    NSString *urlStr = [url absoluteString];
    return urlStr;
}

+ (NSString *)productNameFromFileURL:(NSURL *)fileURL {
    if ([fileURL isFileURL]) {
        NSString *bundlePath = [[fileURL path] stringByDeletingLastPathComponent];
        NSString *rnProductName = [bundlePath lastPathComponent];
        return rnProductName;
    }
    return NULL;
}

- (NSString *)cachedKey {
    if (self.businessURL != nil) {
        return [RCTBridge keyFromURL:self.businessURL];
    }
    else {
        return [RCTBridge keyFromURL:self.bundleURL];
    }
}

- (void)setBridgeState:(eCRNBridgeState)bridgeState {
    objc_setAssociatedObject(self, @selector(bridgeState), [NSNumber numberWithInt:bridgeState], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (eCRNBridgeState)bridgeState {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value intValue];
}


- (void)setOriginalBridgeState:(eCRNBridgeState)originalBridgeState {
    objc_setAssociatedObject(self, @selector(originalBridgeState), [NSNumber numberWithInt:originalBridgeState], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (eCRNBridgeState)originalBridgeState {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value intValue];
}


- (void)setInUseCount:(NSUInteger)inUseCount {
    objc_setAssociatedObject(self, @selector(inUseCount), [NSNumber numberWithUnsignedInteger:inUseCount], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSUInteger)inUseCount {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value unsignedIntegerValue];
}

- (CFAbsoluteTime)createTimestamp {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value doubleValue];
}
- (void)setCreateTimestamp:(CFAbsoluteTime)createTimestamp {
    objc_setAssociatedObject(self, @selector(createTimestamp), [NSNumber numberWithDouble:createTimestamp], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}


- (NSString *)productName {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setProductName:(NSString *)productName {
    objc_setAssociatedObject(self, @selector(productName), productName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSString *)rnProductName {
    if (self.productName.length == 0) {
        if (self.crnURL.isUnbundleRNURL) {
            NSURL *tmpURL = self.businessURL?self.businessURL:self.bundleURL;
            NSString *ret = [RCTBridge productNameFromFileURL:tmpURL];
            if (ret == NULL) {
                ret = [tmpURL path];
            }
            [self setProductName:ret];
        }
    }
    return self.productName;
}

-(CRNURL *)crnURL {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setCrnURL:(CRNURL *)crnURL {
    objc_setAssociatedObject(self, @selector(crnURL), crnURL, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

-(NSURL *)businessURL {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setBusinessURL:(NSURL *)businessURL {
    objc_setAssociatedObject(self, @selector(businessURL), businessURL, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (BOOL)isUnbundlePackage {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value boolValue];
}
- (void)setIsUnbundlePackage:(BOOL)isUnbundlePackage {
    objc_setAssociatedObject(self, @selector(isUnbundlePackage), [NSNumber numberWithBool:isUnbundlePackage], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSTimeInterval)enterViewTime {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value doubleValue];
}
- (void)setEnterViewTime:(NSTimeInterval)enterViewTime {
    objc_setAssociatedObject(self, @selector(enterViewTime), [NSNumber numberWithDouble:enterViewTime], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSTimeInterval)renderDoneTime {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value doubleValue];
}
- (void)setRenderDoneTime:(NSTimeInterval)renderDoneTime {
    objc_setAssociatedObject(self, @selector(renderDoneTime), [NSNumber numberWithDouble:renderDoneTime], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}


- (NSTimeInterval)bridgeInitTime {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value doubleValue];
}
- (void)setBridgeInitTime:(NSTimeInterval)bridgeInitTime {
    objc_setAssociatedObject(self, @selector(bridgeInitTime), [NSNumber numberWithDouble:bridgeInitTime], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSTimeInterval)bridgeReadyTime {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value doubleValue];
}
- (void)setBridgeReadyTime:(NSTimeInterval)bridgeReadyTime {
    objc_setAssociatedObject(self, @selector(bridgeReadyTime), [NSNumber numberWithDouble:bridgeReadyTime], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (BOOL)isRenderSuccess {
    NSNumber *value = objc_getAssociatedObject(self, _cmd);
    return [value boolValue];
}

- (void)setIsRenderSuccess:(BOOL)isRenderSuccess {
    objc_setAssociatedObject(self, @selector(isRenderSuccess), [NSNumber numberWithBool:isRenderSuccess], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSString *)instanceID {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setInstanceID:(NSString *)instanceID {
    objc_setAssociatedObject(self, @selector(instanceID), instanceID, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSMutableDictionary *)pluginObjectsDict {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setPluginObjectsDict:(NSMutableDictionary *)pluginObjectsDict {
    objc_setAssociatedObject(self, @selector(pluginObjectsDict), pluginObjectsDict, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CRNView *)crnView {
    id value = objc_getAssociatedObject(self, _cmd);
    return value;
}
- (void)setCrnView:(CRNView *)crnView {
    objc_setAssociatedObject(self, @selector(crnView), crnView, OBJC_ASSOCIATION_ASSIGN);
}


+ (NSString *)bridgeStateDesc:(eCRNBridgeState)bridgeState {
    NSString *state = @"";
    switch (bridgeState) {
        case Bridge_State_Dirty:
            state = @"Dirty";
            break;
        case Bridge_State_Loading:
            state = @"Loading";
            break;
        case Bridge_State_Ready:
            state = @"Ready";
            break;
        case Bridge_State_Error:
            state = @"Error";
            break;
        default:
            break;
    }
    return state;
}

- (NSString *)description {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:[RCTBridge bridgeStateDesc:self.bridgeState] forKey:@"bridgeState"];
    [dict setValue:[RCTBridge bridgeStateDesc:self.originalBridgeState] forKey:@"originalBridgeState"];
    [dict setValue:self.businessURL forKey:@"pkgURL"];
    [dict setValue:@(self.inUseCount) forKey:@"inUseCount"];
    [dict setValue:@([self hash]) forKey:@"hash"];
    [dict setValue:@([self isRenderSuccess]) forKey:@"isRenderSuccess"];

    return [dict description];
}

@end
