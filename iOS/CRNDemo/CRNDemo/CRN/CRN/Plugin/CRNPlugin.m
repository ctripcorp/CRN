//
//  CRNPlugin.m
//  CTBusiness
//
//  Created by CRN on 15/11/18.
//  Copyright © 2015年 Ctrip. All rights reserved.
//

#import "CRNPlugin.h"

#import "CRNView.h"
#import <objc/message.h>
#import "RCTBridge+CRN.h"

#import <React/RCTUtils.h>
#import <React/RCTRootView.h>
@implementation CRNPlugin

+ (void)callModule:(NSString *)moduleName
          function:(NSString *)functionName
        parameters:(NSDictionary *)parameters
            bridge:(RCTBridge *)bridge
          callback:(RCTResponseSenderBlock)callback
{
    RCTAssert(moduleName, @"module name should not be null.");
    RCTAssert(functionName, @"function name should not be null.");
    NSString *moduleClassName = [[@"CRN" stringByAppendingString:moduleName] stringByAppendingString:@"Plugin"];
    
    CRNPlugin *object = [CRNPlugin pluginObjectForBridge:bridge moduleClass:moduleClassName];
    [object callFunction:functionName parameters:parameters callback:callback];
}

+ (CRNPlugin *)pluginObjectForBridge:(RCTBridge *)bridge moduleClass:(NSString *)moduleClassName{
    if (bridge == NULL || ![moduleClassName isKindOfClass:[NSString class]]) {
        return NULL;
    }
    
    Class cxxBridgeClass = objc_lookUpClass("RCTCxxBridge");
    if ([bridge isKindOfClass:cxxBridgeClass]) {
        bridge = [bridge valueForKey:@"parentBridge"];
    }
    
    CRNPlugin *object = nil;
    if (bridge && [bridge isKindOfClass:[RCTBridge class]]) {
        @synchronized(bridge.pluginObjectsDict){
            object = [bridge.pluginObjectsDict valueForKey:moduleClassName];
        }
    }
    if (object) {
        return object;
    }
    
    Class cls = NSClassFromString(moduleClassName);
    object = [[cls alloc] init];
    object.bridge = bridge;
    
    @synchronized(bridge.pluginObjectsDict){
        [bridge.pluginObjectsDict setValue:object forKey:moduleClassName];
    }
    
    RCTAssert([cls isSubclassOfClass:self], @"can not find the class, module name may be incorrect.");
    return object;
}

- (void)callFunction:(NSString *)functionName
          parameters:(NSDictionary *)parameters
            callback:(RCTResponseSenderBlock)callback {
    //subclass override.
}

- (UIView *)crnView{
    if (_crnView) {
        return _crnView;
    } else {
        if (_bridge && [_bridge isKindOfClass:[RCTBridge class]]) {
            if (_bridge.crnView && [_bridge.crnView isKindOfClass:[CRNView class]]) {
                CRNView *view  = (CRNView *)_bridge.crnView;
                _crnView = view;
                return _crnView;
            }
        }
    }
    return _crnView;
}

- (void)clear {
    //TO be OVERRIDE
}

+ (NSDictionary *)RNResultWithStatusCode:(int)statusCode
                              methodName:(NSString *)methodName
                               errorDesc:(NSString *)errorDesc {
    NSMutableDictionary *statusDict = [NSMutableDictionary dictionary];
    [statusDict setValue:[NSNumber numberWithInt:statusCode]  forKey:@"status"];
    [statusDict setValue:methodName forKey:@"function"];
    if (statusCode != 0 && errorDesc == nil) {
        NSAssert(false, @"StatusCode!=0, 请提供Error 描述!");
    }
    
    if (statusCode != 0 && errorDesc.length > 0) {
        [statusDict setValue:errorDesc forKey:@"errorDesc"];
    }
    
    return statusDict;
}

@end
