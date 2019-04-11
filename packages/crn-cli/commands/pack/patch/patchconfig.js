const config = [
    {
        from: "./metro/crn/crn-as-assets.js",
        to: "node_modules/metro/src/shared/output/RamBundle/crn-as-assets.js",
        comments: "为ram-bundle抽取的crn的逻辑文件"
    },
    {
        from: "./metro/crn/crn-bundle.js",
        to: "node_modules/metro/src/shared/output/crn-bundle.js",
        comments: "为bundle抽取的crn的逻辑文件"
    },
    {
        from: "./metro/crn/crn-createModuleIdFactory.js",
        to: "node_modules/metro/src/lib/createModuleIdFactory.js",
        comments: "修改moduleid mapping"
    },
    {
        from: "./metro/patch/as-assets.js",
        to: "node_modules/metro/src/shared/output/RamBundle/as-assets.js",
        comments: "修改ram-bundle输出"
    },
    {
        from: "./metro/patch/bundle.js",
        to: "node_modules/metro/src/shared/output/bundle.js",
        comments: "修改bundle输出"
    },
    {
        from: "./metro/patch/collectDependencies.js",
        to: "node_modules/metro/src/ModuleGraph/worker/collectDependencies.js",
        comments: "support lazyRequire ,require(event.moudleId)"
    },
    {
        from: "./metro/patch/index.js",
        to: "node_modules/metro-config/src/defaults/index.js",
        comments: "add compress drop_console drop_debugger"
    },
    {
        from: "./metro/patch/RamBundle.js",
        to: "node_modules/metro/src/shared/output/RamBundle.js",
        comments: "ios支持RamBundle"
    },
    {
        from: "./metro/patch/require.js",
        to: "node_modules/metro/src/lib/polyfills/require.js",
        comments: "修改nativeRequire(moduleId, segmentId)"
    },
    {
        from: "./react-native/crn/crn-lazyRequire.js",
        to: "node_modules/react-native/Libraries/polyfills/lazyRequire.js",
        comments: "add lazyRequire source"
    },
    {
        from: "./react-native/patch/AssetSourceResolver.js",
        to: "node_modules/react-native/Libraries/Image/AssetSourceResolver.js",
        comments: "保持Android访问图片资源和IOS一致",
    },
    {
        from: "./react-native/patch/resolveAssetSource.js",
        to: "node_modules/react-native/Libraries/Image/resolveAssetSource.js",
        comments: "fix url query参数中有'/'时拼接本地图片路径出错",
    },
    {
        from: "./react-native-local-cli/patch/bundle.js",
        to: "node_modules/@react-native-community/cli/build/commands/bundle/bundle.js",
        comments: "add global.CRN_BUILD_COMMON",
    },
    {
        from: "./react-native-local-cli/patch/buildBundle.js",
        to: "node_modules/@react-native-community/cli/build/commands/bundle/buildBundle.js",
        comments: "add buildCommon args",
    },
    {
        from: "./react-native-local-cli/patch/bundleCommandLineArgs.js",
        to: "node_modules/@react-native-community/cli/build/commands/bundle/bundleCommandLineArgs.js",
        comments: "add buildCommon args",
    },
    {
        from: "./react-native-local-cli/patch/rn-get-polyfills.js",
        to: "node_modules/react-native/rn-get-polyfills.js",
        comments: "add lazyrequire"
    },
    {
        from: "./crn/rn-cli.config.js",
        to: "./rn-cli.config.js",
        comments: "add rn-cli.config.js",
    },
    {
        from: "./crn/crn_common_entry.js",
        to: "./crn_common_entry.js",
        comments: "add crn_common_entry.js",
    }
];
module.exports = config;
