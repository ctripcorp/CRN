//
//  CRNURL.m
//  CTBusiness
//
//  Created by CRN on 5/26/16.
//  Copyright © 2016 Ctrip. All rights reserved.
//


#import "CRNURL.h"
#import "CRNDefine.h"
#import "RCTBridge+CRN.h"
#import "NSString+URL.h"
#import "CRNUtils.h"


#define kUnbundleFileName   @"_crn_unbundle"

@interface CRNURL()

@property (nonatomic, copy) NSString *fileAbsolutePath;
@property (nonatomic, copy)  NSString *moduleName;
@property (nonatomic, copy)  NSString *title;
@property (nonatomic, strong) NSURL *bundleURL;
@property (nonatomic, copy) NSString *inRelativeURLStr;
@property (nonatomic, strong) NSString *unBundleFilePath;
@property (nonatomic, strong) NSString *productName;

@end

@implementation CRNURL

+ (BOOL)isCRNURL:(NSString *)url {
    NSString *lurl = url.lowercaseString;
    BOOL isCRNCommonURL = [lurl isEqualToString:[[self commonJSURL] absoluteString].lowercaseString];
    BOOL isCRNBizURL = [lurl containsString:kCRNModuleName.lowercaseString] &&
    [lurl containsString:kCRNModuleType.lowercaseString];
    return isCRNBizURL || isCRNCommonURL;

}


+ (NSURL *)commonJSURL {
    return [NSURL fileURLWithPath:[self commonJSPath]];
}

+ (NSString *)commonJSPath {
    return [kWebAppDirPath stringByAppendingFormat:@"%@/%@", kCRNCommonJsBundleDirName, kCRNCommonJsBundleFileName];
}

- (BOOL)isUnbundleRNURL {
    [self readUnbundleFilePathIfNeed];
    return self.unBundleFilePath.length > 0;
}

- (NSString *)unBundleWorkDir {
    return [self.fileAbsolutePath stringByDeletingLastPathComponent];
}

- (id)initWithPath:(NSString *)urlPath {
    if (self = [super init]) {
        self.inRelativeURLStr = urlPath;
        if ([urlPath.lowercaseString hasPrefix:@"http"] || [urlPath.lowercaseString hasPrefix:@"file:"]) {
            self.fileAbsolutePath = urlPath;
            self.bundleURL = [NSURL URLWithString:urlPath];
        }
        else if ([urlPath hasPrefix:@"/"]) {
            NSRange paramRange = [urlPath rangeOfString:@"?"];
            if (paramRange.location != NSNotFound) {
                self.fileAbsolutePath = [urlPath substringToIndex:paramRange.location];
                self.fileAbsolutePath = [kWebAppDirPath stringByAppendingPathComponent:self.fileAbsolutePath];
                
                NSString *queryString = [urlPath substringFromIndex:paramRange.location];
                self.bundleURL = [NSURL fileURLWithPath:self.fileAbsolutePath];
                NSString *urlStr = [self.bundleURL.absoluteString stringByAppendingString:[queryString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
                self.bundleURL = [NSURL URLWithString:urlStr];
            }
            //read unbundle file path
            [self readUnbundleFilePathIfNeed];
            self.productName = [CRNUtils getPackageNameFromURLString:self.fileAbsolutePath];
        }
        
        NSDictionary *query = [self.bundleURL.absoluteString query];
        for (NSString *key in query.allKeys) {
            if ([key.lowercaseString isEqualToString:@"crnmodulename"]) {
                self.moduleName = query[key];
                if ([self.bundleURL isFileURL] && [self isUnbundleRNURL]) {
                    self.moduleName = [RCTBridge productNameFromFileURL:self.bundleURL];
                }
            }
            else if ([key.lowercaseString isEqualToString:@"crntitle"]) {
                self.title = query[key];
            }
        }
    }
    
    return self;
}


- (void)readUnbundleFilePathIfNeed {
    if (self.unBundleFilePath == nil) {
        NSString *unBundlFilePath = [[self.fileAbsolutePath stringByDeletingLastPathComponent]
                                     stringByAppendingPathComponent:kUnbundleFileName];
        if (access([unBundlFilePath UTF8String], 0) == 0) {
            self.unBundleFilePath = unBundlFilePath;
        }
    }
}

- (NSString *)rnFilePath {
    return self.fileAbsolutePath;
}

- (NSString *)rnModuleName {
    return self.moduleName;
}

- (NSURL *)rnBundleURL {
    return self.bundleURL;
}

- (NSString *)rnTitle {
    return self.title;
}

- (NSString *)packageName {
    return self.productName;
}




@end
