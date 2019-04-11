'use strict';

var ip = require('ip');
var fs = require('fs-extra')
var path = require('path');
var Promise = require('promise');
var logOutput = require('../../tools/logOutput');
var currentPath = process.cwd();
var build = require('./build');
var deleteFile = require('../../tools/deleteFile');
var patch = require("./patch");
var printHelp = require('../../tools/printHelp');


/**
 * 1、pack crn_entry_common.js 生成rn_common
 * 2、pack index.js，生成rn_CRNDemo
 * 3、拷贝rn_commmon和rn_CRNDemo到publish
 */
function localPack() {
	return new Promise(resolve => {
		var args = process.argv.slice(2);
		if (args[1] == '--help' || args[1] == '-h' || args[1] == '-H') {
			printHelp('pack');
			resolve();
			return;
		}
		var option = require('minimist')(process.argv.slice(2));
		var packConfig = require(path.resolve(currentPath, 'package.json')).packConfig || {};
		var bundleOutput = option['bundle-output'] || packConfig['bundleOutput'] || 'publish';
		var packageName = option['package-name'] || packConfig['packageName'] || 'CRNDemo';
		var entryFile = option['entry-file'] || packConfig['entryFile'] || 'index.js';
		var dev = option['dev'] || packConfig['dev'] || false;
		deleteFile(path.resolve(currentPath, bundleOutput));
		logOutput.log('remove ' + bundleOutput);
		logOutput.log('begin build...');
		patch();

		setTimeout(() => {
			build([
				'--platform',
				'ios',
				'--entry-file',
				'crn_common_entry.js',
				'--build-common',
				'true',
				'--dev',
				dev
			]);
			fs.copySync(path.resolve(currentPath, './bundle_output/publish/'), path.resolve(currentPath, bundleOutput, 'rn_common'));
			fs.copySync(path.resolve(currentPath, './bundle_output/baseMapping.json'), path.resolve(currentPath, bundleOutput, 'rn_common/baseMapping.json'));
			deleteFile(path.resolve(currentPath, './bundle_output/publish/'));

			build([
				'--platform',
				'ios',
				'--entry-file',
				entryFile,
				'--build-common',
				'false',
				'--dev',
				dev
			]);
			fs.copySync(path.resolve(currentPath, './bundle_output/publish/'), path.resolve(currentPath, bundleOutput, 'rn_' + packageName));
			fs.copySync(path.resolve(currentPath, './bundle_output/buMapping.json'), path.resolve(currentPath, bundleOutput, 'rn_' + packageName, 'buMapping.json'));
			deleteFile(path.resolve(currentPath, './bundle_output'));
			deleteFile(path.resolve(currentPath, './bundle_output_other'));
			logOutput.log('打包完成,请拷贝当前工程目录下' + bundleOutput + '目录文件到app中进行测试');
			resolve();

		}, 100);

	});
}

module.exports = localPack;