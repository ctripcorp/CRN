//
//  CRNUtils.m
//  CTCRN
//
//  Created by CRN on 2019/2/22.
//  Copyright © 2019 zlp. All rights reserved.
//

#import "CRNUtils.h"
#import "CRNDefine.h"
#import "CTToastTipView.h"
#import <React/RCTBridge.h>

@implementation CRNUtils

+ (NSString *)getPackageNameFromURLString:(NSString *)urlString {
    if ([[urlString lowercaseString] hasPrefix:@"http"]) {
        return urlString;
    }
    
    if (urlString.length == 0) {
        return NULL;
    }
    
    NSString *pkgName = NULL;
    if ([urlString hasPrefix:@"http"]) {
        NSURL *url = [NSURL URLWithString:urlString];
        pkgName = url.host;
    }
    else {// if ([currentPageUrl hasPrefix:@"file"])
        NSString *wFlag = [NSString stringWithFormat:@"%@", kWebAppDirName];
        NSRange wRange = [urlString rangeOfString:wFlag];
        if (wRange.location != NSNotFound) {
            NSString *tmpStr = [urlString substringFromIndex:wRange.location + wFlag.length];
            NSRange eRange = [tmpStr rangeOfString:@"/"];
            if (eRange.location != NSNotFound) {
                tmpStr = [tmpStr substringFromIndex:1];
                NSRange fRange = [tmpStr rangeOfString:@"/"];
                if (fRange.location != NSNotFound) {
                    pkgName = [tmpStr substringToIndex:fRange.location];
                }
            }
        }
    }
    return pkgName;
}

+ (void)emitEventForBridge:(RCTBridge *)bridge name:(NSString *)eventName info:(NSDictionary *)info {
    if (bridge.isValid) {
        @try {
            [bridge enqueueJSCall:@"RCTDeviceEventEmitter.emit" args:info ? @[eventName, info] : @[eventName]];
        } @catch (NSException *exception) {
            NSLog(@"%@", exception);
        } @finally {
            
        }
    }
}

+ (void)showToast:(NSString *)text{
    [CTToastTipView showTipText:text];
}



@end
