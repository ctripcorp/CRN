//
//  CRNURL.h
//  CTBusiness
//
//  Created by CRN on 5/26/16.
//  Copyright © 2016 Ctrip. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface CRNURL : NSObject

+ (BOOL)isCRNURL:(NSString *)url;

+ (NSURL *)commonJSURL;

+ (NSString *)commonJSPath;


@property (nonatomic, readonly) NSString *rnFilePath;
@property (nonatomic, readonly)  NSString *rnModuleName;
@property (nonatomic, readonly) NSURL *rnBundleURL;
@property (nonatomic, readonly) NSString *rnTitle;

//是否unbundle
@property (nonatomic, readonly) BOOL isUnbundleRNURL;
@property (nonatomic, readonly) NSString *unBundleWorkDir;

@property (nonatomic, readonly) NSString *packageName;

- (id)initWithPath:(NSString *)urlPath;

- (void)readUnbundleFilePathIfNeed;

@end
