//
//  CRNUtils.h
//  CTCRN
//
//  Created by CRN on 2019/2/22.
//  Copyright © 2019 zlp. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class RCTBridge;

@interface CRNUtils : NSObject

/**
 *@description 从URL中获取模块名
 *@return 返回模块名
 */
+ (NSString *)getPackageNameFromURLString:(NSString *)urlString;

+ (void)emitEventForBridge:(RCTBridge *)bridge name:(NSString *)eventName info:(NSDictionary *)info;

+ (void)showToast:(NSString *)text;
@end

NS_ASSUME_NONNULL_END
