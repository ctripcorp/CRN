//
//  NSString+URL.h
//  CRNDemo
//
//  Created by CRN on 2019/3/27.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (URL)

@property (readonly, strong) NSDictionary *query;

@end

NS_ASSUME_NONNULL_END
