(function () {
    "use strict"
    const keys = keysfromAnd;
    const options = optionsfromAnd;
    const device = deviceinfoFromAnd;
    const listeners = [];
    const option = {
        timeout: 60000
    };
    const genFName = () => {
        const name = 'f' + Math.random().toString(10).substr(2,8);
        if($flex.flex[name] === undefined) {
            return Promise.resolve(name);
        } else {
            return Promise.resolve(genFName());
        }
    }
    const triggerEventListener = (name, val) => {
        listeners.forEach(element => {
            if(element.e === name && typeof element.c === 'function') {
                element.c(val);
            }
        });
    }
    const setOptions = () => {
        Object.keys(options).forEach(k => {
            if(k === 'timeout' && typeof options[k] === 'number') {
                Object.defineProperty(option, k, {
                    value: options[k], writable: false, enumerable: true
                });
            }
        });
    }
    setOptions();
    window.$flex = {};
    Object.defineProperties($flex,
        {
            version: { value: '0.3.9', writable: false, enumerable: true },
            isAndroid: { value: true, writable: false, enumerable: true },
            isiOS: { value: false, writable: false, enumerable: true },
            device: { value: device, writable: false, enumerable: true },
            addEventListener: { value: function(event, callback) { listeners.push({ e: event, c: callback }) }, writable: false, enumerable: false  },
            web: { value: {}, writable: false, enumerable: true },
            options: { value: option, writable: false, enumerable: true },
            flex: { value: {}, writable: false, enumerable: false }
        }
    );
    const define = window.flexdefine;
    delete window.flexdefine;
    keys.forEach(key => {
        if ($flex[key] === undefined) {
            Object.defineProperty($flex, key, {
                value: function (...args) {
                    return new Promise((resolve, reject) => {
                        genFName().then(name => {
                            const counter = setTimeout(() => {
                                $flex.flex[name](false, "timeout error");
                                triggerEventListener('timeout', {
                                    "function": key
                                });
                            }, option.timeout);
                            $flex.flex[name] = (j, e, r) => {
                                clearTimeout(counter);
                                delete $flex.flex[name];
                                if(j) {
                                    resolve(r);
                                } else {
                                    let err;
                                    if(typeof e === 'string') err = Error(e);
                                    else err = '$flex Error occurred in function -- $flex.' + key;
                                    reject(err);
                                    triggerEventListener('error', {
                                        "function" : key,
                                        "err": err
                                    });
                                }
                            };
                            try {
                                define.flexInterface(JSON.stringify({intName:key,funName:name,arguments:args}));
                            } catch (e) {
                                $flex.flex[name](false, e.toString());
                            }
                        });
                    });
                },
                writable: false,
                enumerable: true
            });
        }
    });
    setTimeout(() => {
        if (typeof window.onFlexLoad === 'function') {
            window.onFlexLoad();
        }
    }, 0);
})();