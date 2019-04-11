//
//  CRNPlugin.h
//  CTBusiness
//
//  Created by CRN on 15/11/18.
//  Copyright © 2015年 Ctrip. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CRNDefine.h"
#import <React/RCTBridgeModule.h>
#import <React/RCTAssert.h>

#define CRNResult(s, k, v) [CRNPlugin RNResultWithStatusCode:(s) methodName:(k) errorDesc:(v)]

@interface CRNPlugin : NSObject

@property (nonatomic, weak) RCTBridge *bridge;

@property (nonatomic, weak) UIView *crnView;

+ (void)callModule:(NSString *)moduleName
          function:(NSString *)functionName
        parameters:(NSDictionary *)parameters
            bridge:(RCTBridge *)bridge
          callback:(RCTResponseSenderBlock)callback;

//子类重载，实现对应的plugin功能
- (void)callFunction:(NSString *)functionName
          parameters:(NSDictionary *)parameters
            callback:(RCTResponseSenderBlock)callback;

//子类重载
- (void)clear;

//plugin api调用通用result
+ (NSDictionary *)RNResultWithStatusCode:(int)statusCode
                              methodName:(NSString *)methodName
                               errorDesc:(NSString *)errorDesc;

@end
