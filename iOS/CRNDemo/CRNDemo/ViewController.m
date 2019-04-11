//
//  ViewController.m
//  CRNDemo
//
//  Created by CRN on 2019/1/25.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "ViewController.h"
#import "CRNURLHandler.h"
#import "CRNUtils.h"

@interface ViewController ()<UITextFieldDelegate, UITableViewDataSource, UITableViewDelegate,UIActionSheetDelegate>


@property (nonatomic, strong) NSMutableArray *rnBundles;
@property (nonatomic, strong) NSMutableArray *crnBundles;

@property (nonatomic, copy) NSString *selectedRNBundle;
@property (nonatomic, copy) NSString *selectedCRNBundle;

@property (strong, nonatomic) UITextField *urlField;

@property (nonatomic, strong) UIButton *RNBundlePickerBtn;
@property (nonatomic, strong) UIButton *CRNBundlePickerBtn;

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSMutableArray *itemList;

@end

#define kItemListKey @"kItemListKey"
#define CTColorHex(c) [UIColor colorWithRed:((c>>16)&0xFF)/255.0 green:((c>>8)&0xFF)/255.0 blue:((c)&0xFF)/255.0 alpha:1.0]

@implementation ViewController

- (NSDictionary *)rnModuleNameMap{
    return @{@"rn_rntester":@"RNTesterApp"};
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.navigationController.navigationBar.barTintColor = CTColorHex(0x099fde);
    [self.navigationController.navigationBar setBackgroundImage:[self navBarBackgroungImage] forBarMetrics:UIBarMetricsDefault];

    self.title = @"CRNDemo";

    [self getBundlesList];

    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20, 10, 160, 40)];
    label.text = @"CRN框架预加载开关";
    label.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label];
    
    UISwitch *switch1 = [[UISwitch alloc] initWithFrame:CGRectMake(180, 13, 100, 30)];
    [switch1 addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    BOOL isDisablePreload = [[NSUserDefaults standardUserDefaults] boolForKey:@"disable_common_preload"];
    switch1.on = !isDisablePreload;
    [self.view addSubview:switch1];
    
    UIView *line0 = [[UIView alloc] initWithFrame:CGRectMake(20, 50, [UIScreen mainScreen].bounds.size.width, 0.5)];
    line0.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:line0];
    
    self.CRNBundlePickerBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.CRNBundlePickerBtn.frame = CGRectMake(20, 60, [UIScreen mainScreen].bounds.size.width - 160, 40);
    NSString *ctitle = @"请选择CRNbundle";
    if (self.crnBundles.count > 0) {
        ctitle = [self.crnBundles firstObject];
    }
    [self.CRNBundlePickerBtn setTitle:ctitle forState:UIControlStateNormal];
    self.CRNBundlePickerBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [self.CRNBundlePickerBtn setTitleColor:[UIColor darkTextColor] forState:UIControlStateNormal];
    [self.CRNBundlePickerBtn addTarget:self action:@selector(selectCRNBundle) forControlEvents:UIControlEventTouchUpInside];
    self.CRNBundlePickerBtn.backgroundColor = [UIColor colorWithRed:0.9 green:0.9 blue:0.9 alpha:1];
    self.CRNBundlePickerBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.view addSubview:self.CRNBundlePickerBtn];
    
    UIImageView *iconView2 = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_arrow_down"]];
    iconView2.contentMode = UIViewContentModeScaleAspectFit;
    iconView2.frame = CGRectMake(self.CRNBundlePickerBtn.frame.size.width-30, 10, 20, 20);
    [self.CRNBundlePickerBtn addSubview:iconView2];
    
    UIButton *openCRNBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    openCRNBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-130, 60, 130, 40);
    [openCRNBtn setTitle:@"加载CRNBundle" forState:UIControlStateNormal];
    openCRNBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    openCRNBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [openCRNBtn addTarget:self action:@selector(openCRNBundle) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:openCRNBtn];
    
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(20, 110, [UIScreen mainScreen].bounds.size.width, 0.5)];
    line.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:line];
    
    self.RNBundlePickerBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.RNBundlePickerBtn.frame = CGRectMake(20, 120, [UIScreen mainScreen].bounds.size.width - 160, 40);
    NSString *title = @"请选择RNbundle";
    if (self.rnBundles.count > 0) {
        title = [self.rnBundles firstObject];
    }
    [self.RNBundlePickerBtn setTitle:title forState:UIControlStateNormal];
    self.RNBundlePickerBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [self.RNBundlePickerBtn setTitleColor:[UIColor darkTextColor] forState:UIControlStateNormal];
    [self.RNBundlePickerBtn addTarget:self action:@selector(selectRNBundle) forControlEvents:UIControlEventTouchUpInside];
    self.RNBundlePickerBtn.backgroundColor = [UIColor colorWithRed:0.9 green:0.9 blue:0.9 alpha:1];
    self.RNBundlePickerBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [self.view addSubview:self.RNBundlePickerBtn];
    
    UIImageView *iconView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_arrow_down"]];
    iconView.contentMode = UIViewContentModeScaleAspectFit;
    iconView.frame = CGRectMake(self.RNBundlePickerBtn.frame.size.width-30, 10, 20, 20);
    [self.RNBundlePickerBtn addSubview:iconView];
    
    UIButton *openRNBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    openRNBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-130, 120, 130, 40);
    [openRNBtn setTitle:@"加载RNBundle" forState:UIControlStateNormal];
    openRNBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    openRNBtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [openRNBtn addTarget:self action:@selector(openRNBundle) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:openRNBtn];

    UIView *line2 = [[UIView alloc] initWithFrame:CGRectMake(20, 170, [UIScreen mainScreen].bounds.size.width, 0.5)];
    line2.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:line2];
    
    self.urlField = [[UITextField alloc] initWithFrame:CGRectMake(20, 200, [UIScreen mainScreen].bounds.size.width-80, 45)];
    self.urlField.font = [UIFont systemFontOfSize:14];
    self.urlField.borderStyle = UITextBorderStyleRoundedRect;
    self.urlField.keyboardType = UIKeyboardTypeURL;
    self.urlField.returnKeyType = UIReturnKeyDone;
    self.urlField.delegate = self;
    self.urlField.placeholder = @"请输入CRN测试url";
    self.urlField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    [self.view addSubview:self.urlField];
    
    UIButton *addBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    addBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-60, 200, 45, 40);
    [addBtn setTitle:@"添加" forState:UIControlStateNormal];
    [addBtn addTarget:self action:@selector(addURL) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:addBtn];
    
    NSUserDefaults *dfts = [NSUserDefaults standardUserDefaults];
    if (self.itemList == NULL) {
        self.itemList = [NSMutableArray array];
    }
    [self.itemList addObjectsFromArray:[dfts valueForKey:kItemListKey]];
    
//    NSString *testUrlStr = @"http://localhost:8081/index.bundle?CRNModuleName=CRNDemo&CRNType=1&platform=ios";

    NSString *rnDebugURLPath = @"/tmp/.__RN_Debug_URL.log";
    NSString *testUrlStr = [NSString stringWithContentsOfFile:rnDebugURLPath
                                         encoding:NSUTF8StringEncoding
                                            error:NULL];
    if (testUrlStr.length > 0) {
        if (![self.itemList containsObject:testUrlStr]) {
            [self.itemList addObject:testUrlStr];
        }
    }
    
    
    if ([self.itemList count] == 0) {
        self.tableView.hidden = YES;
    }
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 250, [UIScreen mainScreen].bounds.size.width-20,[UIScreen mainScreen].bounds.size.height-250) style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
    self.edgesForExtendedLayout = UIRectEdgeNone;

}

- (UIImage *)navBarBackgroungImage{
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context =UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [CTColorHex(0x099fde) CGColor]);
    CGContextFillRect(context, rect);
    UIImage *image =UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

- (void)getBundlesList{
    self.rnBundles = [NSMutableArray array];
    self.crnBundles = [NSMutableArray array];
    NSArray* fileArray = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:kWebAppDirPath error:nil];
    for (NSString* fileName in fileArray) {
        NSString *unBundlFilePath = [[kWebAppDirPath stringByAppendingString:fileName] stringByAppendingPathComponent:@"_crn_unbundle"];
        if (access([unBundlFilePath UTF8String], 0) == 0) {
            [self.crnBundles addObject:fileName];
        }else if (![fileName isEqualToString:@"rn_common"]){
            [self.rnBundles addObject:fileName];
        }
    }
    if (self.rnBundles.count > 0) {
        self.selectedRNBundle = [self.rnBundles firstObject];
    }
    if (self.crnBundles.count > 0) {
        self.selectedCRNBundle = [self.crnBundles firstObject];
    }
}


- (IBAction)openRNBundle {
    NSString *moduleName = @"RNTesterApp";
    if ([self.rnModuleNameMap valueForKey:self.selectedRNBundle]) {
        moduleName = [self.rnModuleNameMap valueForKey:self.selectedRNBundle];
    }
    NSString *url = [NSString stringWithFormat:@"/%@/main.js?CRNModuleName=%@&CRNType=1",self.selectedRNBundle,moduleName];
    [CRNURLHandler openURLString:url fromViewController:self];
}

- (IBAction)openCRNBundle {
    NSString *url = [NSString stringWithFormat:@"/%@/main.js?CRNModuleName=CRNApp&CRNType=1",self.selectedCRNBundle];
    [CRNURLHandler openURLString:url fromViewController:self];
}

- (void)switchAction:(UISwitch *)sender {
    if (!sender.on) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"disable_common_preload"];
        [CRNUtils showToast:@"已关闭预加载，请重启App生效"];
    }else{
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"disable_common_preload"];
        [CRNUtils showToast:@"已打开预加载，请重启App生效"];
    }
}

- (void)selectRNBundle{
    UIActionSheet * as = [[UIActionSheet alloc] initWithTitle:nil
                                                     delegate:self
                                            cancelButtonTitle:nil
                                       destructiveButtonTitle:nil
                                            otherButtonTitles:nil];
    as.tag = 10001;
    for (NSString * itemTitle in self.rnBundles){
        [as addButtonWithTitle:itemTitle];
    }
    [as showInView:[UIApplication sharedApplication].keyWindow];
}

- (void)selectCRNBundle{
    UIActionSheet * as = [[UIActionSheet alloc] initWithTitle:nil
                                                     delegate:self
                                            cancelButtonTitle:nil
                                       destructiveButtonTitle:nil
                                            otherButtonTitles:nil];
    as.tag = 10002;
    for (NSString * itemTitle in self.crnBundles){
        [as addButtonWithTitle:itemTitle];
    }
    [as showInView:[UIApplication sharedApplication].keyWindow];
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    NSString * buttonTitle = [actionSheet buttonTitleAtIndex:buttonIndex];
    if (actionSheet.tag == 10001) {
        self.selectedRNBundle = buttonTitle;
        [self.RNBundlePickerBtn setTitle:buttonTitle forState:UIControlStateNormal];
    }else if (actionSheet.tag == 10002){
        self.selectedCRNBundle = buttonTitle;
        [self.CRNBundlePickerBtn setTitle:buttonTitle forState:UIControlStateNormal];
    }
}



- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [self addURL];
    [textField resignFirstResponder];
    return YES;
}

- (void)addURL {
    NSString *urlStr = self.urlField.text;
    [self.urlField resignFirstResponder];
    if (urlStr != NULL && ![self.itemList containsObject:urlStr]) {
        [self.itemList addObject:urlStr];
        NSUserDefaults *dfts = [NSUserDefaults standardUserDefaults];
        [dfts setValue:self.itemList forKey:kItemListKey];
        [dfts synchronize];
        [self.tableView reloadData];
    }
    self.urlField.text = NULL;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


#pragma mark - ---- table datasource
- (BOOL)tableView:(UITableView *)tableView_ canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *urlStr = [self.itemList objectAtIndex:indexPath.row];
    [self.itemList removeObject:urlStr];
    NSUserDefaults *dfts = [NSUserDefaults standardUserDefaults];
    [dfts setObject:self.itemList forKey:kItemListKey];
    [dfts synchronize];
    [self.tableView reloadData];
}

- (NSInteger)tableView:(UITableView *)tableView_ numberOfRowsInSection:(NSInteger)section {
    return [self.itemList count];
}

- (CGFloat)tableView:(UITableView *)tableView_ heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *contentString = [self.itemList objectAtIndex:indexPath.row];
    NSMutableParagraphStyle * paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary * attributes = @{NSFontAttributeName : [UIFont systemFontOfSize:12],
                                  NSParagraphStyleAttributeName : paragraphStyle};
    CGSize size = [contentString boundingRectWithSize:CGSizeMake([UIScreen mainScreen].bounds.size.width-20, MAXFLOAT)
                                              options:NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading
                                           attributes:attributes
                                              context:nil].size;
    return size.height+14;
}

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellId = @"xeesss";
    UITableViewCell *cell = [tableView_ dequeueReusableCellWithIdentifier:cellId];
    UILongPressGestureRecognizer *press = [[cell gestureRecognizers] firstObject];
    if (cell == NULL) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
        [cell.textLabel setFont:[UIFont systemFontOfSize:12]];
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.numberOfLines = 0;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        if (press == NULL) {
            press = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressAction:)];
            [cell addGestureRecognizer:press];
        }
    }
    press.accessibilityLanguage = (id)indexPath;
    cell.textLabel.text = [self.itemList objectAtIndex:indexPath.row];
    return cell;
}

- (void)longPressAction:(UILongPressGestureRecognizer *)gesture {
    NSIndexPath *indexPath = (NSIndexPath*)gesture.accessibilityLanguage;
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    NSString *text = cell.textLabel.text;
    [UIPasteboard generalPasteboard].string = text;
}

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView_ deselectRowAtIndexPath:indexPath animated:YES];
    NSString *urlStr = [self.itemList objectAtIndex:indexPath.row];
    NSURL *url = [NSURL URLWithString:urlStr];
    if ([CRNURL isCRNURL:urlStr]) {
        [CRNURLHandler openURLString:urlStr fromViewController:self];;
    }
    else if ([[UIApplication sharedApplication] canOpenURL:url]) {
        [[UIApplication sharedApplication] openURL:url];
    }
    else {
        NSLog(@"不能跳转该URL");
    }
}

@end
