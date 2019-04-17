//
//  CRNDefine.h
//  CRNDemo
//
//  Created by CRN on 16/11/8.
//  Copyright © 2016年 ctrip. All rights reserved.
//


#import <Foundation/Foundation.h>

#ifndef CRN_DEV
#if DEBUG
#define CRN_DEV 1
#else
#define CRN_DEV 0
#endif
#endif

//work目录
#define kDocumentDir    [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]

#define kWebappDirPrefixName @"webapp_work"

#define kWebAppDirName [kWebappDirPrefixName stringByAppendingFormat:@"_%@", getAppVersion()]
#define kWebAppDirPath  [kDocumentDir stringByAppendingFormat:@"/%@/",kWebAppDirName]


//CRN const
#define kDefaultCRNUnbundleMainModuleName   @"CRNApp"
#define kCRNCommonJsBundleDirName           @"rn_common"
#define kCRNCommonJsBundleFileName          @"common_ios.js"

#define kCRNModuleName      @"CRNModuleName="
#define kCRNModuleType      @"CRNType=1"

//Notifications
#define kCRNViewDidCreateNotification       @"kCRNViewDidCreateNotification"
#define kCRNViewDidReleasedNotification     @"kCRNViewDidReleasedNotification"


#define kCRNStartLoadEvent                  @"CRNStartLoadEvent"
#define kCRNLoadSuccessEvent                @"CRNLoadSuccessEvent"
#define kCRNPageRenderSuccess               @"CRNPageRenderSuccess"

#define CRNViewLoadFailedNotification       @"CRNViewLoadFailedNotification"
#define CRNViewDidRenderSuccess             @"CRNViewDidRenderSuccess"


#define dispatch_main_sync(block)\
    if ([NSThread isMainThread]) {\
        block();\
    } else {\
        dispatch_sync(dispatch_get_main_queue(), block);\
    }

#define dispatch_main_async(block)\
    if ([NSThread isMainThread]) {\
        block();\
    } else {\
        dispatch_async(dispatch_get_main_queue(), block);\
    }


@interface CRNDefine : NSObject

NSString *getAppVersion(void);

@end

#pragma mark - ----


