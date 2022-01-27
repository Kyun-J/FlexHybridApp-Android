(function () {
  "use strict";
  const flexDefine = definefromAnd;
  const keys = keysfromAnd;
  const timeouts = timesfromAnd;
  const options = optionsfromAnd;
  const device = deviceinfoFromAnd;
  const listeners = [];
  const defineFlex = defineflexfromAnd;
  const _option = {
    timeout: 60000,
    flexLoadWait: 100,
  };
  const genFName = () => {
    const name = "f" + Math.random().toString(10).substr(2, 8);
    if ($flex.flex[name] === undefined) {
      return Promise.resolve(name);
    } else {
      return Promise.resolve(genFName());
    }
  };
  const triggerEventListener = (name, val) => {
    listeners.forEach((element) => {
      if (element.e === name && typeof element.c === "function") {
        element.c(val);
      }
    });
  };
  const setOptions = () => {
    Object.keys(options).forEach((k) => {
      if (k === "timeout" && typeof options[k] === "number") {
        Object.defineProperty(_option, k, {
          value: options[k],
          writable: false,
          enumerable: true,
        });
      }
      if (k === "flexLoadWait" && typeof options[k] === "number") {
        Object.defineProperty(_option, k, {
          value: options[k],
          writable: false,
          enumerable: true,
        });
      }
    });
  };
  setOptions();
  let _preSetWeb = {};
  if (window.__flexScript && typeof window.__flexScript === "object") {
    Object.assign(_preSetWeb, window.__flexScript.web);
  } else if (window.$flex && typeof window.$flex.web === "object") {
    _preSetWeb = window.$flex.web;
  }
  Object.defineProperty(window, "$flex", {
    value: {},
    writable: false,
    enumerable: true,
  });
  Object.defineProperties($flex, {
    version: { value: versionFromAnd, writable: false, enumerable: true },
    isMobile: { value: true, writable: false, enumerable: true },
    isAndroid: { value: true, writable: false, enumerable: true },
    isiOS: { value: false, writable: false, enumerable: true },
    isScript: { value: false, writable: false, enumerable: true },
    device: { value: device, writable: false, enumerable: true },
    addEventListener: {
      value: function (event, callback) {
        listeners.push({ e: event, c: callback });
      },
      writable: false,
      enumerable: false,
    },
    web: { value: _preSetWeb, writable: false, enumerable: true },
    options: { value: _option, writable: false, enumerable: true },
    flex: { value: {}, writable: false, enumerable: false },
  });
  const define = window[flexDefine];
  delete window[flexDefine];
  keys.forEach((key) => {
    if ($flex[key] === undefined) {
      Object.defineProperty($flex, key, {
        value: function (...args) {
          return new Promise((resolve, reject) => {
            genFName().then((name) => {
              let counter;
              let wait = 0;
              if (typeof timeouts[key] !== "undefined" && timeouts[key] !== 0) {
                wait = timeouts[key];
              } else if (_option.timeout !== 0) {
                wait = _option.timeout;
              }
              if (wait !== 0) {
                counter = setTimeout(() => {
                  delete $flex.flex[name];
                  reject("timeout error");
                  $flex.flexTimeout(key, location.href);
                  triggerEventListener("timeout", {
                    function: key,
                  });
                }, wait);
              }
              $flex.flex[name] = (j, e, r) => {
                delete $flex.flex[name];
                if (typeof counter !== "undefined") clearTimeout(counter);
                if (j) {
                  resolve(r);
                  if (!defineFlex.includes(key)) {
                    $flex.flexSuccess(key, location.href, r);
                    triggerEventListener("success", {
                      function: key,
                      data: r,
                    });
                  }
                } else {
                  let err;
                  if (typeof e === "string") err = Error(e);
                  else err = "$flex Error occurred in function -- $flex." + key;
                  reject(err);
                  if (!defineFlex.includes(key)) {
                    $flex.flexException(key, location.href, err.toString());
                    triggerEventListener("error", {
                      function: key,
                      err: err,
                    });
                  }
                }
              };
              try {
                define.flexInterface(
                  JSON.stringify({
                    intName: key,
                    funName: name,
                    arguments: args,
                  })
                );
              } catch (e) {
                $flex.flex[name](false, e.toString());
              }
            });
          });
        },
        writable: false,
        enumerable: false,
      });
    }
  });
  setTimeout(() => {
    let f = () => {};
    if (typeof window.onFlexLoad === "function") {
      f = window.onFlexLoad;
    }
    Object.defineProperty(window, "onFlexLoad", {
      set: function (val) {
        if (typeof val === "function") {
          window._onFlexLoad = val;
          Promise.resolve(val()).then((_) => {
            setTimeout(() => {
              $flex.flexload();
            }, _option.flexLoadWait);
          });
        }
      },
      get: function () {
        return window._onFlexLoad;
      },
    });
    const evalFrames = (w) => {
      for (let i = 0; i < w.frames.length; i++) {
        const wf = w.frames[i];
        delete wf.flexdefine;
        if (typeof wf.$flex === "undefined") {
          Object.defineProperty(wf, "$flex", {
            value: window.$flex,
            writable: false,
            enumerable: true,
          });
          let f = undefined;
          if (typeof wf.onFlexLoad === "function") {
            f = wf.onFlexLoad;
          }
          Object.defineProperty(wf, "onFlexLoad", {
            set: function (val) {
              if (typeof val === "function") {
                wf._onFlexLoad = val;
                val();
              }
            },
            get: function () {
              return wf._onFlexLoad;
            },
          });
          if (typeof f === "function") {
            wf.onFlexLoad = f;
          }
        }
        evalFrames(wf);
      }
    };
    window.onFlexLoad = f;
    evalFrames(window);
    $flex.flexInit("INIT", location.href);
  }, 0);
})();
