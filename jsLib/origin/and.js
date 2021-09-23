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
    web: { value: {}, writable: false, enumerable: true },
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
    window.onFlexLoad = f;
    const evalFrames = (w) => {
      for (let i = 0; i < w.frames.length; i++) {
        delete w.frames[i].flexdefine;
        if (typeof w.frames[i].$flex === "undefined") {
          Object.defineProperty(w.frames[i], "$flex", {
            value: window.$flex,
            writable: false,
            enumerable: true,
          });
          let f = undefined;
          if (typeof w.frames[i].onFlexLoad === "function") {
            f = w.frames[i].onFlexLoad;
          }
          Object.defineProperty(w.frames[i], "onFlexLoad", {
            set: function (val) {
              window.onFlexLoad = val;
            },
            get: function () {
              return window._onFlexLoad;
            },
          });
          if (typeof f === "function") {
            w.frames[i].onFlexLoad = f;
          }
        }
        evalFrames(w.frames[i]);
      }
    };
    evalFrames(window);
    $flex.flexInit("INIT", location.href);
  }, 0);
})();
