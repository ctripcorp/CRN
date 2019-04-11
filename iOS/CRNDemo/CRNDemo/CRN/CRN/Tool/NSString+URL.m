//
//  NSString+URL.m
//  CRNDemo
//
//  Created by CRN on 2019/3/27.
//  Copyright © 2019 com.ctrip. All rights reserved.
//

#import "NSString+URL.h"

@implementation NSString (URL)

- (NSString *)scheme
{
    NSString *scheme = nil;
    if ([self containsString:@"://"]) {
        NSArray *array = [self componentsSeparatedByString:@"://"];
        scheme = array.firstObject;
    }
    
    return scheme;
}

- (NSDictionary *)query
{
    NSMutableDictionary *query = nil;
    NSArray *array = self.queryArray;
    for (NSDictionary *item in array) {
        if (!query) {
            query = [NSMutableDictionary dictionary];
        }
        [query setValue:[item.allValues objectAtIndex:0] forKey:[item.allKeys objectAtIndex:0]];
    }
    
    return query;
}

- (NSString *)queryString
{
    NSString *queryString = nil;
    NSString *resourceSpecifier = self.resourceSpecifier;
    NSString *tempString = [resourceSpecifier substringFromIndex:2];
    NSString *tempString2 = [[tempString componentsSeparatedByString:@"#"] firstObject];
    NSArray *array = [tempString2 componentsSeparatedByString:@"?"];
    if (array.count > 1) {
        tempString2 = array.lastObject;
        queryString = tempString2;
    }
    
    if (!queryString){
        tempString2 = [[tempString componentsSeparatedByString:@"#"] lastObject];
        NSArray *array = [tempString2 componentsSeparatedByString:@"?"];
        if (array.count > 1) {
            tempString2 = array.lastObject;
            queryString = tempString2;
        }
    }
    
    return queryString;
}

- (NSArray *)queryArray
{
    NSMutableArray *queryArray = nil;
    NSString *queryString = self.queryString;
    NSArray *array = [queryString componentsSeparatedByString:@"&"];
    for (NSString *item in array) {
        NSRange range = [item rangeOfString:@"="];// 这里不用数组处理是因为 value 中如可能带有'=',比如 base64 编码的字符串。
        if (range.location != NSNotFound && range.length > 0) {
            NSString *key = [item substringToIndex:range.location];
            NSString *value = [item substringFromIndex:range.location + range.length];
            if (key && value) {
                if (!queryArray) {
                    queryArray = [NSMutableArray array];
                }
                [queryArray addObject:@{key : value }];
            }
        }
    }
    
    return [NSArray arrayWithArray:queryArray];
}

- (NSString *)resourceSpecifier
{
    NSString *resourceSpecifier = nil;
    NSString *scheme = self.scheme;
    if (scheme && [self containsString:scheme]) {
        resourceSpecifier = [self substringFromIndex:scheme.length + 1];
    } else if ([self hasPrefix:@"/"] || [self hasPrefix:@"file:///"]) {
        resourceSpecifier = nil;
    }
    
    return resourceSpecifier;
}



@end
