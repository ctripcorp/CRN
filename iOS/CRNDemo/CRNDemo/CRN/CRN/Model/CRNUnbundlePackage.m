//
//  CRNPackage.m
//  CTBusiness
//
//  Created by CRN on 16/7/21.
//  Copyright © 2016年 Ctrip. All rights reserved.
//

#import "CRNUnbundlePackage.h"
#import "CRNURL.h"

#define kUnbundleConfigFileNameV2         @"_crn_config_v2"
#define kUnbundleConfigMainModuleName   @"main_module"
#define kUnbundleConfigRequirePathName  @"module_path"

@interface CRNUnbundlePackage()
@property (nonatomic, strong) NSDictionary *moduleIdConfig;
@property (nonatomic, strong) NSString *entryModuleId;
@property (nonatomic, strong) NSString *requirePath;
@property (nonatomic, strong) CRNURL *rnURL;
@end

@implementation CRNUnbundlePackage

- (CRNUnbundlePackage *)initWithURL:(CRNURL *)url {
    if (self = [super init]) {
        if (!url.isUnbundleRNURL) {
            NSAssert(false, @"Error: Unbundle package not support online HTTP ReactNative Package!");
        }
        
        //获取文件名
        NSString *unbundleWorkDir = url.unBundleWorkDir;
        NSString *configPath = [unbundleWorkDir stringByAppendingPathComponent:kUnbundleConfigFileNameV2];

        NSError *ioError = NULL;
        NSString *configStr = [NSString stringWithContentsOfFile:configPath encoding:NSUTF8StringEncoding error:&ioError];
        
        NSString *parseErrorStr = NULL;
        //解析raw map
        NSMutableDictionary *rawItemDict = [NSMutableDictionary dictionary];
        NSArray *itemList = [configStr componentsSeparatedByString:@"\n"];
        for (NSString *item in itemList) {
            NSArray *subItemList = [item componentsSeparatedByString:@"="];
            if ([subItemList count]  == 2) {
                [rawItemDict setValue:subItemList[1] forKey:subItemList[0]];
            }
            else {
                if ([subItemList count] > 1) {
                    NSLog(@"Error Unbundle config item:%@",item);
                    NSAssert(false, @"Error: Ilegal Unbundle config item!");
                    parseErrorStr = configStr;
                }
            }
        }
        
        //获取moduleName和requirePath
        self.requirePath = rawItemDict[kUnbundleConfigRequirePathName];
        self.entryModuleId = rawItemDict[kUnbundleConfigMainModuleName];
      
        NSString *unbundleJSModuleDir = [unbundleWorkDir stringByAppendingPathComponent:self.requirePath];
        
        //格式化moduleId和文件路径
        self.moduleIdConfig = @{@"modulePath":unbundleJSModuleDir};
    }
    
    return self;
}

- (NSString *)mainModuleId {
    return self.entryModuleId;
}

- ( NSDictionary *)moduleIdDict {
    return self.moduleIdConfig;
}

@end
