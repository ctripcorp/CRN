//
//  CRNDispatcher.m
//  CTBusiness
//
//  Created by CRN on 5/16/16.
//  Copyright © 2016 Ctrip. All rights reserved.
//

#import "CRNURLHandler.h"
#import "CRNViewController.h"
#import "CRNURL.h"


@implementation CRNURLHandler

+ (BOOL)openURLString:(NSString *)urlString fromViewController:(UIViewController *)vc
{
    if (![CRNURL isCRNURL:urlString] || vc == NULL) {
        return NO;
    }
    CRNURL *url = [[CRNURL alloc] initWithPath:urlString];
    return [self openURL:url fromViewController:vc];
}

+ (BOOL)openURL:(CRNURL *)url fromViewController:(UIViewController *)vc
{
    if (url == NULL || vc == NULL) {
        return NO;
    }
    
    CRNViewController *rvc = [[CRNViewController alloc] initWithURL:url];

    rvc.title = url.rnTitle;
    BOOL ret = NO;
    BOOL isAnimated = YES;
    NSString *urlStr = url.rnBundleURL.absoluteString;

    if ([[self class] isShowTypePresent:urlStr]) {
        [vc.navigationController presentViewController:rvc animated:isAnimated completion:nil];
        ret = YES;
    }
    else{
        [vc.navigationController pushViewController:rvc animated:isAnimated];
        ret = YES;
    }
    return ret;
}

+ (BOOL)isShowTypePresent:(NSString *)urlStr{
    return [urlStr.lowercaseString containsString:@"showType=present".lowercaseString];
}


@end
