//
//  CRNDispatcher.h
//  CTBusiness
//
//  Created by CRN on 5/16/16.
//  Copyright © 2016 Ctrip. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CRNDefine.h"
#import "CRNURL.h"

@interface CRNURLHandler : NSObject

// CRNURL分发
+ (BOOL)openURLString:(NSString *)urlString fromViewController:(UIViewController *)vc;

+ (BOOL)openURL:(CRNURL *)url fromViewController:(UIViewController *)vc;

@end
