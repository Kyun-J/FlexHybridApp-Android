const webpack = require("webpack");

const config = {
  mode: process.env.NODE_ENV,
  context: __dirname + "/origin",
  entry: {
    "and": "./and.js",
  },
  output: {
    path: __dirname + "/dist",
    filename: "[name].js",
  },
  resolve: {
    extensions: [".js"],
  },
  module: {
    rules: [
      {
        test: /\.(js)$/,
        loader: "babel-loader",
        exclude: /node_modules/,
      },
    ],
  },
  plugins: [
    new webpack.DefinePlugin({
      global: "window",
    })
  ],
};

if (config.mode === "production") {
  config.plugins = (config.plugins || []).concat([
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: '"production"',
      },
    }),
  ]);
}

module.exports = config;
