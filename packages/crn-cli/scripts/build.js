/**
 * 拷贝ios和android源码工程到crn-cli
 */
var fs = require('fs-extra');
var path =require('path');

console.log('begin build...');
fs.copySync(path.resolve(process.cwd(),'../../Android'), path.resolve(process.cwd(),'./commands/init/template/android'));
fs.copySync(path.resolve(process.cwd(),'../../iOS/CRNDemo'), path.resolve(process.cwd(),'./commands/init/template/ios'));
console.log('end build...');
