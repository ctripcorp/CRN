//
//  CRNView.h
//  CRNDemo
//
//  Created by CRN on 2019/3/5.
//  Copyright © 2019 com.ctrip. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "CRNURL.h"
#import <React/RCTRootView.h>



@class CRNView;

@protocol CRNViewLoadingDelegate <NSObject>
@optional
- (void)showLoadingView;
- (void)hideLoadingView;
- (void)showLoadFailViewWithCode:(NSNumber *)code;
@end

@protocol CRNViewDelegate <NSObject>
@optional
- (void)crnViewLoadFailed:(CRNView *)view errorCode:(NSNumber *)code;
- (void)crnViewWillAppear:(CRNView *)view;

@end

@interface CRNView : UIView

@property (nonatomic, weak) id<CRNViewDelegate> viewDelegate;
@property (nonatomic, weak) id<CRNViewLoadingDelegate> loadingDelegate;
@property (nonatomic, readonly) RCTBridge *bridge;

@property (nonatomic, readonly) RCTRootView *reactRootView;

//初始化
- (id)initWithURL:(CRNURL *)rnURL
            frame:(CGRect)frame
initialProperties:(NSDictionary *)props
    launchOptions:(NSDictionary *)options;


- (void)loadCRNViewWithURL:(CRNURL *)url_;

@end



