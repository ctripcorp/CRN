//
//  CTFullScreenMaskView.h
//  CTRIP_WIRELESS
//
//  Created by CRN on 12-9-20.
//
//  全屏遮罩类

#import <UIKit/UIKit.h>

@interface CTFullScreenMaskView : UIView

@property (nonatomic, assign) BOOL isHideWhenTouchBackground;//点击是否关闭

- (void)hide;//隐藏

@end
