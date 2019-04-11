//
//  CRNBridgeManager.h
//  CRNDemo
//
//  Created by CRN on 2019/3/5.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "RCTBridge+CRN.h"
#import "CRNURL.h"


@interface CRNBridgeManager : NSObject <RCTBridgeDelegate>

+ (CRNBridgeManager *)sharedCRNBridgeManager;

//根据URL，判断是否有缓存的bridge
+ (BOOL)hasInUseBridgeForURL:(CRNURL*)url;
+ (void)invalidateDirtyBridgeForURL:(CRNURL *)url;

//根据URL，获取Bridge
- (RCTBridge *)bridgeForURL:(CRNURL *)url
             viewCreateTime:(double)viewCreateTime
             moduleProvider:(RCTBridgeModuleListProvider)block
               launchOption:(NSDictionary *)options;

//预加载框架Bridge
- (void)prepareBridgeIfNeed;

@end

