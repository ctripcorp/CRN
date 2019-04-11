//
//  CTFullScreenMaskView.m
//  CTRIP_WIRELESS
//
//  Created by NickJackson on 12-9-20.
//
//  全屏遮罩类

#import "CTFullScreenMaskView.h"

@implementation CTFullScreenMaskView

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        _isHideWhenTouchBackground = YES;
    }
    
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        _isHideWhenTouchBackground = YES;
    }
    
    return self;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    if (self.isHideWhenTouchBackground) {
        [self hide]; 
    }
}

- (void)hide
{
    [UIView animateWithDuration:.5f animations:^{
        [self setAlpha:0.0f];
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

@end
