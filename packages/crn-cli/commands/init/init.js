'use strict';

const fs = require('fs');
const path = require('path');
const Promise = require("promise");
const spawnSync = require('child_process').spawnSync;
const logOutput = require('../../tools/logOutput');
const printHelp = require("../../tools/printHelp");
const fss = require('fs-extra');

function init(args) {
	return new Promise(resolve => {
		var args = process.argv.slice(2);
		if (args.length < 2 || args[1] == '--help' || args[1] == '-h' || args[1] == '-H') {
			printHelp('init');
			resolve();
			return;
		}
		var projectName = args[1];
		var options = require('minimist')(process.argv.slice(2));
		if (!projectName) {
			logOutput.error('init requires a project name.');
			printHelp('init');
			process.exit(1);
		}

		var projectPath = path.resolve(projectName);
		if (fs.existsSync(projectPath)) {
			logOutput.warn('project name is already exist');
		} else {
			fs.mkdirSync(projectPath);
		}

		logOutput.log('begin init...');
		fss.copySync(path.resolve(__dirname, './template'), projectPath)
		process.chdir(projectPath);
		logOutput.log('create package.json');
		createPackageJSON(projectName);
		logOutput.log('create app.json');
		createAppJSON(projectName);
		logOutput.log('install dependencies');
		installDependencies('16.8.3', '0.59.0');
		logOutput.log('end init...');
		resolve();
	});
}

function createPackageJSON(projectName = 'CRNDemo') {
	var packageJson = {
		name: projectName,
		version: '0.0.1',
		private: true,
		scripts: {
			start: 'crn start',
			android: 'crn run-android',
			ios: 'crn run-ios',
			pack:'crn pack'
		},
		packConfig:{
			entryFile:'index.js',
			bundleOutput:'publish',
			packageName:'CRNDemo',
			dev:false
		}
	};
	fs.writeFileSync(path.resolve('package.json'), JSON.stringify(packageJson));
}

function createAppJSON(projectName = 'CRNDemo') {
	var appJSON = {
		"name": projectName,
		"displayName": projectName
	};
	fs.writeFileSync(path.resolve('app.json'), JSON.stringify(appJSON, null, 2));
}

function installDependencies(reactVersion, rnVersion) {
	try {
		var params = [
			'install',
			'--save',
			'--save-exact',
			`react@${reactVersion}`,
			`react-native@${rnVersion}`,
			'@react-native-community/cli@1.4.5',//固定cli,metro版本
			"metro@0.51.0",
			"metro-config@0.51.0",
			"metro-core@0.51.0",
			"metro-memory-fs@0.51.0",
			"metro-react-native-babel-transformer@0.51.0"
		];
		var npmCmd = /^win/.test(process.platform) ? 'npm.cmd' : 'npm';

		logOutput.log(npmCmd + ' ' + params.join(' '));
		var result = spawnSync(npmCmd, params, { stdio: 'inherit' });
		if (result && result.error) {
			throw result.error;
		}
		params = [
			'install',
			'-D',
			'@babel/core',
			'@babel/runtime',
			'metro-react-native-babel-preset'
		];
		logOutput.log(npmCmd + ' ' + params.join(' '));
		result = spawnSync(npmCmd, params, { stdio: 'inherit' });
		if (result && result.error) {
			throw result.error;
		}
	} catch (error) {
		logOutput.error('install dependencies error');
		if (error) {
			logOutput.error(error.stack || error.message);
		}
		process.exit(1);
	}
}

module.exports = init;
