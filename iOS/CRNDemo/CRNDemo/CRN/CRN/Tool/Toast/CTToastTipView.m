//
//  CTToastTipView.m
//  CTRIP_WIRELESS
//
//  Created by NickJackson on 13-8-27.
//  Copyright (c) 2013年 携程. All rights reserved.
//  Toast 提示框视图类

#import "CTToastTipView.h"
#import <QuartzCore/QuartzCore.h>
#import "CTFullScreenMaskView.h"

@interface CTToastTipView()

@property (nonatomic, strong) CTFullScreenMaskView *maskView;

@property (nonatomic, copy) NSString *tipText;
@property (nonatomic, strong) UILabel *tipLabel;

@end

@implementation CTToastTipView

#pragma mark - Life Circle
- (void)dealloc
{
    [_maskView removeFromSuperview];
    _maskView = nil;
}

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self initBaseView];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self initBaseView];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    if (_tipLabel) {
        int edge = 10;
        [_tipLabel setFrame:UIEdgeInsetsInsetRect(self.bounds, UIEdgeInsetsMake(edge, edge, edge, edge))];
    }
}

- (void)initBaseView
{
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.7]];
    
    [self addSubview:self.tipLabel];
    
    [self setClipsToBounds:YES];
    [self.layer setCornerRadius:5];
}

- (void)willMoveToWindow:(UIWindow *)newWindow
{
    [super willMoveToWindow:newWindow];
    
    if (newWindow) {
        self.tipLabel.text = self.tipText;
    }
}

#pragma mark - Interface

#pragma mark - 在默认 key window 上显示 toast
+ (void)showTipText:(NSString *)text
{
    [CTToastTipView showTipText:text withWidth:0 originY:0 cornerRadius:0 color:nil displayTime:0 needMask:YES inView:nil];
}

#pragma mark - Private
+ (void)showTipText:(NSString *)text
          withWidth:(CGFloat)width
             originY:(CGFloat)originY
       cornerRadius:(CGFloat)cornerRadius
              color:(UIColor*)color
        displayTime:(NSTimeInterval)displayTime
           needMask:(BOOL)needMask
             inView:(UIView *)view
{
    UIFont *font = [UIFont systemFontOfSize:15];
    
    CGFloat aWidth = width;
    if (fabs(aWidth) < 1e-6) {
        aWidth = 250.0;//默认宽度
    }
    
    CGFloat aCornerRadius = cornerRadius;
    if (fabs(aCornerRadius) < 1e-6) {
        aCornerRadius = 4.0;//默认圆角大小
    }
    
    UIColor *aColor = color;
    if (!aColor) {
        aColor = [[UIColor blackColor] colorWithAlphaComponent:0.7];//默认背景色
    }
    
    NSTimeInterval aDisplayTime = displayTime;
    if (fabs(aDisplayTime) < 1e-6) {
        aDisplayTime = 2.5;//默认停留显示时间
    }
    
    UIView *aView = view;
    if (!aView) {
        aView = [[[UIApplication sharedApplication] delegate] window];//默认加在 window 上
    }
    
    CGFloat aOriginY = originY;
    if (fabs(aOriginY) < 1e-6) {
        aOriginY = aView.frame.size.height / 2.0;//默认 originY 位置
    }
    
    CGSize textSize = [text boundingRectWithSize:CGSizeMake(aWidth - 20, 320) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:font} context:nil].size;
    CTToastTipView *toastTipView = [[CTToastTipView alloc] initWithFrame:CGRectMake(0, 0, aWidth, MAX(44, textSize.height + 8))];
    if (textSize.height > 20) {
        int viewHeight = 15 + textSize.height + 15;
        CGRect toastFrame = toastTipView.frame;
        toastFrame.size.height = viewHeight;
        toastTipView.frame = toastFrame;
    }
    
    toastTipView.tipText = text;
    toastTipView.tipLabel.font = font;
    
    [toastTipView setCenter:CGPointMake(aView.bounds.size.width / 2.0, aOriginY)];
    toastTipView.layer.opacity = 0.0;
    
    if (needMask) {
        if (!toastTipView.maskView) {
            toastTipView.maskView = [[CTFullScreenMaskView alloc] initWithFrame:view.bounds];
        }
        [toastTipView.maskView addSubview:toastTipView];
        [aView addSubview:toastTipView.maskView];
    } else {
        [aView addSubview:toastTipView];
    }
    
    toastTipView.layer.cornerRadius = aCornerRadius;
    toastTipView.layer.masksToBounds = YES;
    [toastTipView setBackgroundColor:aColor];
    
    [toastTipView fadeInAnimationAfterDelay:aDisplayTime];
}

- (void)fadeInAnimationAfterDelay:(NSTimeInterval)delay
{
    CFTimeInterval fadeinDuration = 0.2;//渐入动画时间
    [CATransaction begin];
    
    [CATransaction setCompletionBlock:^{
        //[self performSelector:@selector(fadeOutAnimation) withObject:nil afterDelay:delay];
        __weak typeof (self) ws = self;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, delay*NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [ws fadeOutAnimation];
        });
    }];
    
    CABasicAnimation *fadeInAnimation = [CABasicAnimation animationWithKeyPath:@"opacity"];
    [fadeInAnimation setDuration:fadeinDuration];
    [fadeInAnimation setFromValue:[NSNumber numberWithFloat:0.0]];
    [fadeInAnimation setToValue:[NSNumber numberWithFloat:1.0]];
    [fadeInAnimation setRemovedOnCompletion:NO];
    [fadeInAnimation setFillMode:kCAFillModeForwards];
    [fadeInAnimation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear]];
    
    [self.layer addAnimation:fadeInAnimation forKey:@"fadeIn"];
    
    [CATransaction commit];
}

- (void)fadeOutAnimation
{
    CFTimeInterval fadeoutDuration = 0.3;//渐出动画时间
    [CATransaction begin];
    
    [CATransaction setCompletionBlock:^{
        [self forceHide];
    }];
    
    CABasicAnimation *fadeOutAnimation = [CABasicAnimation animationWithKeyPath:@"opacity"];
    [fadeOutAnimation setDuration:fadeoutDuration];
    [fadeOutAnimation setFromValue:[NSNumber numberWithFloat:1.0]];
    [fadeOutAnimation setToValue:[NSNumber numberWithFloat:0.0]];
    [fadeOutAnimation setRemovedOnCompletion:NO];
    [fadeOutAnimation setFillMode:kCAFillModeForwards];
    [fadeOutAnimation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear]];
    
    [self.layer addAnimation:fadeOutAnimation forKey:@"fadeOut"];
    
    [CATransaction commit];
}

- (void)forceHide
{
    [NSObject cancelPreviousPerformRequestsWithTarget:self ];
    
    [self removeFromSuperview];
    [_maskView removeFromSuperview];
    _maskView = nil;
}

- (UILabel *)tipLabel
{
    if (!_tipLabel) {
        _tipLabel = [[UILabel alloc] initWithFrame:UIEdgeInsetsInsetRect(self.bounds, UIEdgeInsetsMake(2, 10, 2, 10))];
        [_tipLabel setBackgroundColor:[UIColor clearColor]];
        [_tipLabel setTextAlignment:NSTextAlignmentCenter];
        [_tipLabel setTextColor:[UIColor whiteColor]];
        [_tipLabel setFont:[UIFont systemFontOfSize:15]];
        _tipLabel.numberOfLines = INT_MAX;
    }
    
    return _tipLabel;
}
@end
