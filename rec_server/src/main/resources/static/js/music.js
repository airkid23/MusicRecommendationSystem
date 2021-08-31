//主框架
(function (w) {
    function PF(select) {
        return this.init(select);
    }

    PF.prototype.trim = function (val) {
        return val.replace(/^\s+|\s+$$/g, '');
    };
    PF.prototype.pushArry = function (dom, result) {
        for (var i = 0; i < dom.length; i++) {
            result.push(dom[i]);
        }
    };
    PF.prototype.$id = function (id, parent) {
        return (parent || document).getElementById(id);
    };
    PF.prototype.$class = function (name, parent) {
        if (document.getElementsByClassName) {
            return (parent || document).getElementsByClassName(name);
        }
        var doms = (parent || document).getElementsByTagName("*");
        var arr = [];
        for (var i = 0; i < doms.length; i++) {
            var nm = doms[i].className.split(' ');
            for (var j = 0; j < nm.length; j++) {
                if (name == this.trim(nm[j])) {
                    arr.push(doms[i]);
                    console.log(doms[i]);
                }
            }
        }
        return arr;
    };
    PF.prototype.$tag = function (tag, parent) {
        return (parent || document).getElementsByTagName(tag);
    };
    PF.prototype.$group = function (name) {
        var names = this.trim(name.split(','));
        var dom = [];
        var result = [];
        var that = this;
        for (var j = 0; j < names.length; j++) {
            var first = names[j].charAt(0);
            if (first == '#') {
                dom = [that.$id(names[j].slice(1))];
            } else if (first == '.') {
                dom = that.$class(names[j].slice(1));
            } else {
                dom = that.$tag(names[j]);
            }
            this.pushArry(dom, result);
        }
        return result;
    };
    PF.prototype.$cengCi = function (name) {
        var names = this.trim(name).split(' ');
        var parent = [], result = [];
        for (var i = 0; i < names.length; i++) {
            result = [];
            var dom = this.trim(names[i]);
            var first = dom.charAt(0);
            if (first == '#') {
                this.pushArry([this.$id(dom.slice(1))], result);
            } else if (first == '.') {
                if (parent.length) {
                    for (var j = 0; j < parent.length; j++) {
                        this.pushArry(this.$class(dom.slice(1), parent[j]), result);
                    }
                } else {
                    this.pushArry(this.$class(dom.slice(1)), result);
                }
            } else {
                if (parent.length) {
                    for (var j = 0; j < parent.length; j++) {
                        this.pushArry(this.$tag(dom, parent[j]), result);
                    }
                } else {
                    this.pushArry(this.$tag(dom), result);
                }
            }
            parent = result;
        }
        return result;
    };
    PF.prototype.$select = function (name) {
        var result = [];
        var names = this.trim(name).split(',');
        for (var i = 0; i < names.length; i++) {
            var dom = this.trim(names[i]);
            var content = this.$cengCi(dom);
            this.pushArry(content, result);
        }
        return result;
    };
    PF.prototype.init = function (select) {
        var that = this;
        that.length = 0;
        if (!select) {
            return that;
        }
        if ("string" == typeof select) {
            var doms = null;
            if (select.indexOf(',') < 1 && select.indexOf(' ') < 1) {
                var first = select.charAt(0);
                if (first == '#') {
                    that[0] = that.$id(select.slice(1));
                    that.length++;
                } else if (first == '.') {
                    doms = that.$class(select.slice(1));
                    for (var i = 0; i < doms.length; i++) {
                        that[i] = doms[i];
                    }
                    that.length = doms.length;
                } else {
                    doms = that.$tag(select);
                    for (var i = 0; i < doms.length; i++) {
                        that[i] = doms[i];
                    }
                    that.length = doms.length;
                }
            } else {
                doms = that.$select(select);
                for (var i = 0; i < doms.length; i++) {
                    that[i] = doms[i];
                }
                that.length = doms.length;
            }
        } else if (select.nodeType) {
            that[0] = select;
            that.length++;
        }
        return that;
    };
    var pf = function (select) {
        if ('function' == typeof select) {
            window.onload = select;
        } else {
            return new PF(select);
        }
    };
    pf.extend = function () {
        var target = null;
        var arg = arguments;
        if (arg.length == 0) {
            return;
        } else if (arg.length == 1) {
            target = PF.prototype;
            for (var item in arg[0]) {
                target[item] = arg[0][item];
            }
        } else {
            target = arg[0];
            for (var i = 1; i < arg.length; i++) {
                for (var item in arg[i]) {
                    target[item] = arg[i][item];
                }
            }
        }
    };
    w.$$ = pf;
})(window);
//公共
(function (w) {
    //需要链式访问
    w.extend({
        each: function (fn) {
            var that = this;
            for (var i = 0; i < that.length; i++) {
                fn.call(that[i]);
            }
            return that;
        }
    });
    //不需要链式访问或访问原型
    w.extend(w, {
        //去空格
        trim: function (val) {
            return val.replace(/^\s+|\s+$$/g, '');
        }
    })
})($$);
//DOM
(function (w) {
    //需要链式访问
    w.extend({
        add: function (dom) {
            this[this.length] = dom;
            this.length++;
            return this;
        },
        append: function (child) {
            var dom = w(child);
            this.each(function () {
                for (var j = 0; j < dom.length; j++) {
                    this.appendChild(dom[j]);
                }
            })
        },
        appendTo: function (parent) {
            var dom = w(parent);
            for (var j = 0; j < dom.length; j++) {
                this.each(function () {
                    dom[j].appendChild(this);
                })
            }
            return this;
        },
        //获取dom
        get: function (i) {
            return $$(this[i]);
        },
        eq: function (i) {
            return $$(this[i]);
        },
        children: function () {
            var child = [];
            this.each(function () {
                var dom = this.children();
                for (var j = 0; j < dom.length; j++) {
                    child.push(dom[j]);
                }
            });
            for (var k = 0; k < this.length; K++) {
                delete this[k];
            }
            for (var l = 0; l < child.length; l++) {
                this[l] = child[l];
            }
            this.length = child.length;
            return this;

        },
        parent: function () {
            var pt = this[0].parentNode;
            for (var i = 0; i < this.length; i++) {
                delete this[i];
            }
            this[0] = pt;
            this.length = 1;
            return this;
        },
        index: function () {
            return getIndex(this[0]);
            function getIndex(obj){
                var children = obj.parentNode.children;
                for (var i = 0; i < children.length; i++){
                    if (children[i] == obj){
                        return i;
                    }
                }
            }
        }
    });
    //不需要访问原型
    w.extend(w, {
        //获取事件对象
        getEvent: function (e) {
            return e || window.event;
        },
        //获取元素
        getTarget: function (e) {
            return this.getEvent(e).target || this.getEvent(e).srcElement;
        },
        //阻止冒泡及捕获
        stopPropagation: function (e) {
            var event = this.getEvent(e);
            if (event.stopPropagation) {
                event.stopPropagation();
            } else {
                event.cancelBubble = true;
            }
        },
        //阻止默认行为
        preventDefault: function (e) {
            var event = this.getEvent(e);
            if (event.preventDefault) {
                event.preventDefault();
            } else {
                event.returnValue = false;
            }
        },
        //鼠标滚动
        getDetail: function (event) {
            var event = this.getEvent(event);
            if (event.wheelDelta) {
                return event.wheelDelta / 120;
            } else {
                return -event.detail / 3;
            }
        }
    })
})($$);
//属性 内容
(function (w) {
    w.extend({
        attr: function () {
            var arg = arguments;
            if (arg.length < 1) {
                return;
            } else if (arg.length == 1) {
                if (typeof arg[0] == 'string') {
                    return this[0].getAttribute(arg[0]);
                } else if (typeof  arg[0] == 'object') {
                    for (var item in arg[0]) {
                        this.each(function () {
                            this.setAttribute(item, arg[0][item]);
                        })
                    }
                }
            } else if (arg.length == 2) {
                this.each(function () {
                    this.setAttribute(arg[0], arg[1]);
                })
            }
            return this;
        },
        addClass: function (val) {
            val = w.trim(val);
            this.each(function () {
                if (this.className.indexOf(val) < 1) {
                    this.className = w.trim(this.className + ' ' + val);
                }
            });
            return this;
        },
        hasClass: function (val) {
            if (!val) {
                return;
            }
            val = w.trim(val);
            var names = this[0].className.split(' ');
            for (var i = 0; i < names.length; i++) {
                if (names[i] == val) {
                    return true;
                }
            }
            return false;
        },
        removeClass: function (val) {
            val = w.trim(val);
            this.each(function () {
                this.className = w.trim(this.className.replace(val, ''));
            });
            return this;
        },
        toggleClass: function (val) {
            val = w.trim(val);
            this.each(function () {
                if (this.className.indexOf(val) < 1) {
                    this.className = w.trim(this.className + ' ' + val);
                } else {
                    this.className = w.trim(this.className.replace(val, ''));
                }
            });
            return this;
        },

        html: function () {
            var arg = arguments
            if (arg.length == 0) {
                return this[0].innerHTML;
            } else if (arg.length == 1) {
                this.each(function () {
                    this.innerHTML = arg[0];
                })
            }
            return this;
        }
    });
})($$);
//事件
(function (w) {
    w.extend({
        on: function (type, fn) {
            if (document.addEventListener) {
                this.each(function () {
                    this.addEventListener(type, fn);
                })
            } else if (document.attchEvent) {
                this.each(function () {
                    this.attachEvent('on' + type, fn);
                })
            } else {
                this.each(function () {
                    this['on' + type] = fn;
                })
            }
            return this;
        },
        un: function (type, fn) {
            if (document.removeEventListener) {
                this.each(function () {
                    this.removeEventListener(type, fn);
                });
            //}
            //else if (document.detachEvent) {
            //    //this.each(function () {
            //    //    this.detachEvent('on' + type, fn);
            //    //})
            } else {
                this.each(function () {
                    this["on" + type] = null;
                });
            }
            return this;
        },
        click: function (fn) {
            this.on('click', fn);
            return this;
        },
        mouseover: function (fn) {
            this.on('mouseover', fn);
            return this;
        },
        mouseout: function (fn) {
            this.on('mouseout', fn);
            return this;
        },
        mousemove: function (fn) {
            this.on('mousemove', fn);
            return this;
        },
        hover: function (fnOver, fnOut) {
            if (fnOver) {
                this.mouseover(fnOver);
            }
            if (fnOut) {
                this.mouseout(fnOut);
            }
            return this;
        }
    })
})($$);
//css
(function (w) {
    w.extend({
        css: function () {
            var arg = arguments;
            if (arg.length < 1) {
                return;
            } else if (arg.length == 1) {
                if (typeof arg[0] == 'string') {
                    if (this[0].currentStyle) {
                        return this[0].currentStyle[arg[0]];
                    } else {
                        return getComputedStyle(this[0], null)[arg[0]];
                    }
                } else if (typeof arg[0] == 'object') {
                    this.each(function () {
                        for (var item in arg[0]) {
                            this.style[item] = arg[0][item];
                        }
                    })
                }
            } else if (arg.length == 2) {
                this.each(function () {
                    this.style[arg[0]] = arg[1];
                })
            }
            return this;
        },
        hide: function () {
            this.each(function () {
                this.style.display = 'none';
            });
            return this;
        },
        show: function () {
            this.each(function () {
                this.style.display = 'block';
            });
            return this;
        }
    })
})($$);

//运动 ({需要运动的属性名及其运动后属性值}，运动时间，加速类型）
(function (w) {
    w.extend({
        animate: function (json, times, ease,fn) {
            var interval = 30;
            var that = this[0];
            var timer = '';
            var allStyle = getAll(json, times, ease);//处理获得的数据
            run();
            //获得css相关属性
            function getStyle(json) {
                var target = [];
                for (var item in json) {
                    var obj = {};
                    obj.start = parseFloat($$(that).css(item)) || 0;
                    obj.distance = parseFloat(json[item]) - obj.start;
                    obj.property = item;
                    target.push(obj);
                }
                return target;
            }

            //运动对象所有属性
            function getAll(json, times, ease) {
                var obj = {};
                obj.times = times;
                obj.ease = ease;
                obj.style = getStyle(json);
                obj.now = +new Date();
                return obj;
            }

            //单个属性改变(属性名，起始属性，移动距离，进程度)
            function oneProterty(style, start, distance, tween) {
                if (style == 'opacity') {
                    $$(that).css(style, start + distance * tween);
                } else {
                    $$(that).css(style, start + distance * tween + 'px');
                }
            }

            //多个属性改变
            function manyProterty(styles, tween) {
                for (var i = 0; i < styles.length; i++) {
                    oneProterty(styles[i].property, styles[i].start, styles[i].distance, tween);
                }
            }

            //运动部分
            function move() {
                var pass = +new Date();
                var tween = getTween(allStyle.now, pass, allStyle.times, allStyle.ease);
                //console.log(tween);
                if (tween >= 1) {
                    stop(allStyle.style,fn);
                } else {
                    manyProterty(allStyle.style, tween);
                }
            }

            //开始运动
            function run() {
                timer = setInterval(move, interval);
            }

            //停止运动
            function stop(styles,fn) {
                manyProterty(styles, 1);
                clearInterval(timer);
                timer = null;
                if (fn){
                    fn();
                }
            }

            //进程
            function getTween(now, pass, times, ease) {
                var eases = {
                    //线性匀速
                    linear: function (t, b, c, d) {
                        return (c - b) * (t / d);
                    },
                    //弹性运动
                    easeOutBounce: function (t, b, c, d) {
                        if ((t /= d) < (1 / 2.75)) {
                            return c * (7.5625 * t * t) + b;
                        } else if (t < (2 / 2.75)) {
                            return c * (7.5625 * (t -= (1.5 / 2.75)) * t + .75) + b;
                        } else if (t < (2.5 / 2.75)) {
                            return c * (7.5625 * (t -= (2.25 / 2.75)) * t + .9375) + b;
                        } else {
                            return c * (7.5625 * (t -= (2.625 / 2.75)) * t + .984375) + b;
                        }
                    },
                    //其他
                    swing: function (t, b, c, d) {
                        return this.easeOutQuad(t, b, c, d);
                    },
                    easeInQuad: function (t, b, c, d) {
                        return c * (t /= d) * t + b;
                    },
                    easeOutQuad: function (t, b, c, d) {
                        return -c * (t /= d) * (t - 2) + b;
                    },
                    easeInOutQuad: function (t, b, c, d) {
                        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
                        return -c / 2 * ((--t) * (t - 2) - 1) + b;
                    },
                    easeInCubic: function (t, b, c, d) {
                        return c * (t /= d) * t * t + b;
                    },
                    easeOutCubic: function (t, b, c, d) {
                        return c * ((t = t / d - 1) * t * t + 1) + b;
                    },
                    easeInOutCubic: function (t, b, c, d) {
                        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
                        return c / 2 * ((t -= 2) * t * t + 2) + b;
                    },
                    easeInQuart: function (t, b, c, d) {
                        return c * (t /= d) * t * t * t + b;
                    },
                    easeOutQuart: function (t, b, c, d) {
                        return -c * ((t = t / d - 1) * t * t * t - 1) + b;
                    },
                    easeInOutQuart: function (t, b, c, d) {
                        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
                        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
                    },
                    easeInQuint: function (t, b, c, d) {
                        return c * (t /= d) * t * t * t * t + b;
                    },
                    easeOutQuint: function (t, b, c, d) {
                        return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
                    },
                    easeInOutQuint: function (t, b, c, d) {
                        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
                        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
                    },
                    easeInSine: function (t, b, c, d) {
                        return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;
                    },
                    easeOutSine: function (t, b, c, d) {
                        return c * Math.sin(t / d * (Math.PI / 2)) + b;
                    },
                    easeInOutSine: function (t, b, c, d) {
                        return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
                    },
                    easeInExpo: function (t, b, c, d) {
                        return (t == 0) ? b : c * Math.pow(2, 10 * (t / d - 1)) + b;
                    },
                    easeOutExpo: function (t, b, c, d) {
                        return (t == d) ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b;
                    },
                    easeInOutExpo: function (t, b, c, d) {
                        if (t == 0) return b;
                        if (t == d) return b + c;
                        if ((t /= d / 2) < 1) return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
                        return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
                    },
                    easeInCirc: function (t, b, c, d) {
                        return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b;
                    },
                    easeOutCirc: function (t, b, c, d) {
                        return c * Math.sqrt(1 - (t = t / d - 1) * t) + b;
                    },
                    easeInOutCirc: function (t, b, c, d) {
                        if ((t /= d / 2) < 1) return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
                        return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
                    },
                    easeInElastic: function (t, b, c, d) {
                        var s = 1.70158;
                        var p = 0;
                        var a = c;
                        if (t == 0) return b;
                        if ((t /= d) == 1) return b + c;
                        if (!p) p = d * .3;
                        if (a < Math.abs(c)) {
                            a = c;
                            var s = p / 4;
                        }
                        else var s = p / (2 * Math.PI) * Math.asin(c / a);
                        return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
                    },
                    easeOutElastic: function (t, b, c, d) {
                        var s = 1.70158;
                        var p = 0;
                        var a = c;
                        if (t == 0) return b;
                        if ((t /= d) == 1) return b + c;
                        if (!p) p = d * .3;
                        if (a < Math.abs(c)) {
                            a = c;
                            var s = p / 4;
                        }
                        else var s = p / (2 * Math.PI) * Math.asin(c / a);
                        return a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
                    },
                    easeInOutElastic: function (t, b, c, d) {
                        var s = 1.70158;
                        var p = 0;
                        var a = c;
                        if (t == 0) return b;
                        if ((t /= d / 2) == 2) return b + c;
                        if (!p) p = d * (.3 * 1.5);
                        if (a < Math.abs(c)) {
                            a = c;
                            var s = p / 4;
                        }
                        else var s = p / (2 * Math.PI) * Math.asin(c / a);
                        if (t < 1) return -.5 * (a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
                        return a * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p) * .5 + c + b;
                    },
                    easeInBack: function (t, b, c, d, s) {
                        if (s == undefined) s = 1.70158;
                        return c * (t /= d) * t * ((s + 1) * t - s) + b;
                    },
                    easeOutBack: function (t, b, c, d, s) {
                        if (s == undefined) s = 1.70158;
                        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
                    },
                    easeInOutBack: function (t, b, c, d, s) {
                        if (s == undefined) s = 1.70158;
                        if ((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
                        return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
                    },
                    easeInBounce: function (t, b, c, d) {
                        return c - this.easeOutBounce(d - t, 0, c, d) + b;
                    },
                    easeInOutBounce: function (t, b, c, d) {
                        if (t < d / 2) return this.easeInBounce(t * 2, 0, c, d) * .5 + b;
                        return this.easeOutBounce(t * 2 - d, 0, c, d) * .5 + c * .5 + b;
                    }
                };
                //console.log(pass-now,times,eases.linear(pass - now, 0, 1, times));
                return eases[ease](pass - now, 0, 1, times);
            }
        }
    });
})($$);
