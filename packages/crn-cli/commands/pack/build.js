'use strict';

var fs = require('fs-extra')
var path = require('path');
var util = require('util');
var chalk = require('chalk');
var currentPath = process.cwd();
var execSync = require("child_process").execSync;;
var logOutPut = require("../../tools/logOutput");
var deleteFile = require("../../tools/deleteFile");

function build(options) {
    fs.ensureDirSync(path.resolve(process.cwd(), './bundle_output/publish'))

    var str = options.toString();
    str = str.replace(/(\-\-platform,([ios|android|all]+))/, '--platform,ios');
    options = str.split(',');
    // build ios
    doBuildBundle(options);
    if (str.indexOf("--build-common,false") > -1) {
        fs.copySync('./bundle_output', './bundle_output_other');
    }

    str = str.replace(/(\-\-platform,([ios|android|all]+))/, '--platform,android');
    options = str.split(',');
    // build android
    doBuildBundle(options);
    if (str.indexOf("--build-common,false") > -1) {
        mergeBundle();
    }
    logOutPut.log("pack has done!");
}

/**
 * 生成js-diffs文件夹，存储IOS和Android打包差异化代码
 */
function mergeBundle() {
    var command = "diff -q " + path.resolve(currentPath, 'bundle_output/publish/js-modules/') + " " + path.resolve(currentPath, 'bundle_output_other/publish/js-modules/');
    logOutPut.log(command);
    try {
        execSync(command);
    } catch (error) {
        var diff = error.stdout.toString()
        logOutPut.log(diff);
        var regexp = /6+[0-9]*.js/g;
        var arr = diff.match(regexp)
        arr = [...new Set(arr)];
        logOutPut.log(arr);
        var dest = path.resolve(currentPath, 'bundle_output_other/publish/js-diffs/');
        fs.ensureDirSync(dest)
        arr.forEach(function (item, i) {
            var src = path.resolve(currentPath, 'bundle_output/publish/js-modules/' + item);
            fs.copySync(src, path.resolve(dest, item));
        });
    } finally {
        deleteFile(path.resolve(currentPath, './bundle_output/'));
        fs.copySync('./bundle_output_other', './bundle_output');
        logOutPut.log('mergeBundle finish');
    }
}

function doBuildBundle(args) {
    var options = require('minimist')(args);
    var buildCommand = getBuildCommands(options);
    var buildCommon = options['build-common'];
    if (!util.isBoolean(buildCommon)) {
        buildCommon = buildCommon === 'true';
    }
    var cmd = '';
    if (buildCommon) {
        cmd = 'node node_modules/react-native/local-cli/cli.js bundle --config rn-cli.config.js ' + buildCommand;
        logOutPut.log(cmd);
        execSync(cmd, { stdio: 'inherit' });
    } else {
        cmd = 'node node_modules/react-native/cli.js ram-bundle --config rn-cli.config.js ' + buildCommand + ' --assets-dest bundle_output/publish';
        logOutPut.log(cmd);
        execSync(cmd, { stdio: 'inherit' });
    }

}
function getBuildCommands(options) {
    var platform = options['platform'] || 'ios';
    return [
        '--platform', platform,
        '--bundle-output', (platform == 'ios' ? 'common_ios.js' : 'common_android.js'),
        '--build-common', (options['build-common'] || false),
        '--entry-file', (options['entry-file'] || 'index.js'),
        '--dev', options['dev'],
    ].join(' ');
}
module.exports = build;

