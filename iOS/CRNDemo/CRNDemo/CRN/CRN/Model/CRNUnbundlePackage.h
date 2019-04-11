//
//  CRNPackage.h
//  CTBusiness
//
//  Created by CRN on 16/7/21.
//  Copyright © 2016年 Ctrip. All rights reserved.
//

#import "CRNPlugin.h"
#import "CRNURL.h"

@interface CRNUnbundlePackage : NSObject

- (CRNUnbundlePackage *)initWithURL:(CRNURL *)url;

//主入口moduleid
@property (nonatomic, readonly) NSString *mainModuleId;

//moduleId和js文件的mapping关系
@property (nonatomic, readonly) NSDictionary *moduleIdDict;

@end
