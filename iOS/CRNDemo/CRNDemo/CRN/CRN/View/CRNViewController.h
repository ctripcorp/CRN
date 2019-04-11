//
//  CRNViewController.h
//  CRNDemo
//
//  Created by CRN on 2019/3/5.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CRNDefine.h"

#import <UIKit/UIKit.h>
#import "CRNURL.h"
#import "CRNView.h"

#import <React/RCTBridgeModule.h>


@interface CRNViewController : UIViewController

@property (nonatomic, readonly) CRNURL *crnURL;

- (instancetype)initWithURL:(CRNURL *)url;

- (instancetype)initWithURL:(CRNURL *)url
       andInitialProperties:(NSDictionary *)initialProperties;

@end

