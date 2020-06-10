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
        if(window[name] === undefined) {
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
            version: { value: '0.3.8', writable: false, enumerable: true },
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
                                    name: key
                                });
                            }, option.timeout);
                            $flex.flex[name] = (j, e, r) => {
                                if(j) {
                                    resolve(r);
                                } else {
                                    if(typeof e === 'string') reject(Error(e));
                                    else reject(Error('$flex Error occurred in function -- $flex.' + key));
                                }
                                clearTimeout(counter);
                                delete $flex.flex[name];
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
                enumerable: false
            });
        }
    });
    setTimeout(() => {
        if (typeof window.onFlexLoad === 'function') {
            window.onFlexLoad();
        }
    }, 0);
})();