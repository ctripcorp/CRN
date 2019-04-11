/**
 * @description 打印cli各种命令帮助
 * @param {string} type 帮助类型 eg pack,init...
 */
function printHelp(type, data) {
    switch (type) {
        case "cli":
            console.log(
                [
                    "Usage: crn-cli <command> [options]",
                    "Commands:",
                    "   init                   建立并初始化CRN工程，基于React Native 0.59.0,React 16.8.3",
                    "   start                  启动CRN服务,默认端口5389",
                    "   run-ios                启动IOS模拟器，运行App",
                    "   run-android            运行Android App",
                    "   pack                   打包，生成common包和biz包",
                    "Options:",
                    "   -h, --help             显示命令帮助",
                    "   -v, --version          显示版本",
                    "",
                    ""
                ].join("\n")
            );
            break;
        case "init":
            console.log(
                [
                    "",
                    " Usage: crn-cli init <project-name>",
                    "",
                    " Example:",
                    "",
                    " $ crn-cli init Demo ",
                    "",
                ].join("\n")
            );
            break;
        case "start":
            console.log(
                [
                    "",
                    " Usage: crn-cli start [options]",
                    "",
                    " Options:",
                    "",
                    " --port           本地服务端口,默认5389",
                    "",
                    " Example:",
                    "",
                    " $ crn-cli start --port 5389 ",
                    "",
                ].join("\n")
            );
            break;
        case "run-ios":
            console.log(
                [
                    "",
                    " Usage: crn-cli run-ios [options]",
                    "",
                    " Options:",
                    "",
                    " --port           本地服务端口,默认5389",
                    " --url            启动页面路径        ",
                    "",
                    " Example:",
                    "",
                    " $ crn-cli run-ios --port 5389 --url /rn_CRNDemo/crn_config?CRNType=1&CRNModuleName=xxx",
                    "",
                ].join("\n")
            );
            break;
        case "run-android":
            console.log(
                [
                    "",
                    " Usage: crn-cli run-android [options]",
                    "",
                    " Options:",
                    "",
                    " --port           本地服务端口,默认5389",
                    " --url            启动页面路径        ",
                    "",
                    " Example:",
                    "",
                    " $ crn-cli run-android --port 5389 --url /rn_CRNDemo/crn_config?CRNType=1&CRNModuleName=xxx",
                    "",
                ].join("\n")
            );
            break;
        case "pack":
            console.log(
                [
                    "",
                    " Usage: crn-cli pack [options]",
                    "",
                    " Options:",
                    "",
                    " --entry-file      业务包入口文件,默认index.js",
                    " --package-name    业务包名称,默认CRNDemo",
                    " --dev             打包环境,默认false",
                    " --bundle-output   打包输出目录,默认publish",
                    "",
                    " Example:",
                    "",
                    " $ crn-cli pack --entry-file index.js --package-name CRNDemo --dev false --bundle-output publish",
                    "",
                ].join("\n")
            );
            break;
        default:
    }

}
module.exports = printHelp;