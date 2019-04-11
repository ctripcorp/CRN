//
//  AppDelegate.m
//  CRNDemo
//
//  Created by CRN on 2019/1/25.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "AppDelegate.h"
#import "ViewController.h"
#import "CRNBridgeManager.h"
#import "CRNURLHandler.h"
#import "CRNUtils.h"
#import "CRNDefine.h"

@interface AppDelegate ()

@property (nonatomic, strong) ViewController *vc;

@end


@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    [self copyPackages];
    BOOL isDisablePreload = [[NSUserDefaults standardUserDefaults] boolForKey:@"disable_common_preload"];
    if (!isDisablePreload) {
        //预加载common
        [[CRNBridgeManager sharedCRNBridgeManager] prepareBridgeIfNeed];
    }
    [self initWindow];
    
#if CRN_DEV
    BOOL isRNDev = NO;
    NSString *rnDebugURLPath = @"/tmp/.__RN_Debug_URL.log";
    NSString *rnURLStr = nil;
    rnURLStr = [NSString stringWithContentsOfFile:rnDebugURLPath
                                         encoding:NSUTF8StringEncoding
                                            error:NULL];
    NSURL *rnURL = [NSURL URLWithString:rnURLStr];
    if (rnURL) {
        isRNDev = YES;
    }
    if (isRNDev){
        [CRNUtils showToast:[NSString stringWithFormat:@"将进入RN：%@",rnURLStr]];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [CRNURLHandler openURLString:rnURLStr fromViewController:self.vc];
            remove([rnDebugURLPath UTF8String]);
        });
    }
#endif
    
    return YES;
}

//拷贝打包产物到工作目录
- (void)copyPackages {
    NSLog(@"webapp dir=%@",kWebAppDirPath);
    NSString *bundleDir = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"webapp"];
    NSFileManager *fm = [NSFileManager defaultManager];
    NSError *error = NULL;
    if (![fm fileExistsAtPath:kWebAppDirPath]) {
        [fm createDirectoryAtPath:kWebAppDirPath withIntermediateDirectories:YES attributes:NULL error:&error];
    }
    if (error == NULL) {
        NSArray *items = [fm contentsOfDirectoryAtPath:bundleDir error:&error];
        for (NSString *item in items) {
            NSString *fromFullPath = [bundleDir stringByAppendingPathComponent:item];
            NSString *destFullPath = [kWebAppDirPath stringByAppendingPathComponent:item];
            if ([fm fileExistsAtPath:destFullPath]) {
                [fm removeItemAtPath:destFullPath error:&error];
            }
            if (error == NULL) {
                [fm copyItemAtPath:fromFullPath toPath:destFullPath error:&error];
            }
            if (error) {
                break;
            }
        }
    }
    if(error != NULL) {
        NSLog(@"Copy Packages失败-%@",error.localizedDescription);
    } else {
        NSLog(@"Copy Packages完成！");
    }
}

- (void)initWindow{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    ViewController *vc = [[ViewController alloc] init];
    self.vc = vc;
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
    self.window.rootViewController = nav;
    
    [self.window makeKeyAndVisible];
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


@end
