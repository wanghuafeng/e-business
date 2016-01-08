/*
Copyright 2013, KISSY v1.40
MIT Licensed
build time: Nov 21 23:47
*/

var window = window || {};
var navigator = navigator || {};


var KISSY = function (a) {
    function e(a) {
        var f = {};
        g.each(k, function (e, b) {
            f[b] = function (c) {
                return g.log(c, b, a)
            }
        });
        return f
    }
    var g, l = 0,
        k = {
            debug: 10,
            info: 20,
            warn: 30,
            error: 40
        };
    g = {
        __BUILD_TIME: "20131121234724",
        Env: {
            host: this
        },
        Config: {
            debug: "",
            fns: {}
        },
        version: "1.40",
        config: function (d, e) {
                var n, b, c = this,
                    p, h = g.Config,
                    q = h.fns;
                g.isObject(d) ? g.each(d, function (a, b) {
                    (p = q[b]) ? p.call(c, a): h[b] = a
                }) : (n = q[d], e === a ? b = n ? n.call(c) : h[d] : n ? b = n.call(c, e) : h[d] = e);
                return b
            }, log: function () {
                return a
            }, getLogger: function (a) {
                return e(a)
            },
            error: function () {}, guid: function (a) {
                return (a || "") + l++
            }, Logger: {}
    };
    g.Logger.Level = {
        DEBUG: "debug",
        INFO: "info",
        WARN: "warn",
        ERROR: "error"
    };
    return g
}();
(function (a, e) {
    function g() {}

    function l(c, j, b, m, i, h) {
        if (!j || !c) return c;
        var p, t, f, g;
        j[d] = c;
        h.push(j);
        f = a.keys(j);
        g = f.length;
        for (p = 0; p < g; p++)
            if (t = f[p], t != d) {
                var k = c,
                    x = j,
                    E = b,
                    F = m,
                    H = i,
                    L = h;
                if (E || !(t in k) || H) {
                    var w = k[t],
                        u = x[t];
                    if (w === u) w === e && (k[t] = w);
                    else if (F && (u = F.call(x, t, u)), H && u && (a.isArray(u) || a.isPlainObject(u))) u[d] ? k[t] = u[d] : (x = w && (a.isArray(w) || a.isPlainObject(w)) ? w : a.isArray(u) ? [] : {}, k[t] = x, l(x, u, E, F, n, L));
                    else if (u !== e && (E || !(t in k))) k[t] = u
                }
            }
        return c
    }

    function k(a, c) {
        return "constructor" ==
            a ? e : c
    }
    var d = "__MIX_CIRCULAR",
        f = this,
        n = !0,
        b = Object,
        c = b.create,
        p = !{
            toString: 1
        }.propertyIsEnumerable("toString"),
        h = "constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toString,toLocaleString,valueOf".split(",");
    (function (a, c) {
        for (var b in c) a[b] = c[b]
    })(a, {
        stamp: function (c, b, o) {
            var o = o || "__~ks_stamped",
                m = c[o];
            if (!m && !b) try {
                m = c[o] = a.guid(o)
            } catch (i) {
                m = e
            }
            return m
        }, keys: b.keys || function (a) {
            var c = [],
                b, m;
            for (b in a) a.hasOwnProperty(b) && c.push(b);
            if (p)
                for (m = h.length - 1; 0 <= m; m--) b = h[m], a.hasOwnProperty(b) &&
                    c.push(b);
            return c
        }, mix: function (c, b, o, m, i) {
            "object" === typeof o && (m = o.whitelist, i = o.deep, o = o.overwrite);
            if (m && "function" !== typeof m) var h = m,
                m = function (c, b) {
                    return a.inArray(c, h) ? b : e
                };
            o === e && (o = n);
            var p = [],
                t = 0;
            for (l(c, b, o, m, i, p); b = p[t++];) delete b[d];
            return c
        }, merge: function (c) {
            var c = a.makeArray(arguments),
                b = {},
                o, m = c.length;
            for (o = 0; o < m; o++) a.mix(b, c[o]);
            return b
        }, augment: function (c, b) {
            var o = a.makeArray(arguments),
                m = o.length - 2,
                i = 1,
                h, d, p = o[m],
                f = o[m + 1];
            a.isArray(f) || (p = f, f = e, m++);
            "boolean" !== typeof p &&
                (p = e, m++);
            for (; i < m; i++) {
                d = o[i];
                if (h = d.prototype) d = a.mix({}, h, !0, k);
                a.mix(c.prototype, d, p, f)
            }
            return c
        }, extend: function (b, j, o, m) {
            if (!j || !b) return b;
            var i = j.prototype;
            i.constructor = j;
            c ? j = c(i) : (g.prototype = i, j = new g);
            j.constructor = b;
            b.prototype = a.mix(j, b.prototype);
            b.superclass = i;
            o && a.mix(j, o);
            m && a.mix(b, m);
            return b
        }, namespace: function () {
            var c = a.makeArray(arguments),
                b = c.length,
                o = null,
                m, i, h, d = c[b - 1] === n && b--;
            for (m = 0; m < b; m++) {
                h = ("" + c[m]).split(".");
                o = d ? f : this;
                for (i = f[h[0]] === o ? 1 : 0; i < h.length; ++i) o = o[h[i]] =
                    o[h[i]] || {}
            }
            return o
        }
    })
})(KISSY);
(function (a, e) {
    var g = Array.prototype,
        l = g.indexOf,
        k = g.lastIndexOf,
        d = g.filter,
        f = g.every,
        n = g.some,
        b = g.map;
    a.mix(a, {
        each: function (c, b, h) {
            if (c) {
                var d, j, o = 0;
                d = c && c.length;
                j = d === e || "function" == a.type(c);
                h = h || null;
                if (j)
                    for (j = a.keys(c); o < j.length && !(d = j[o], !1 === b.call(h, c[d], d, c)); o++);
                else
                    for (j = c[0]; o < d && !1 !== b.call(h, j, o, c); j = c[++o]);
            }
            return c
        }, indexOf: l ? function (a, b) {
            return l.call(b, a)
        } : function (a, b) {
            for (var h = 0, d = b.length; h < d; ++h)
                if (b[h] === a) return h;
            return -1
        }, lastIndexOf: k ? function (a, b) {
            return k.call(b,
                a)
        } : function (a, b) {
            for (var d = b.length - 1; 0 <= d && b[d] !== a; d--);
            return d
        }, unique: function (b, d) {
            var h = b.slice();
            d && h.reverse();
            for (var q = 0, j, o; q < h.length;) {
                for (o = h[q];
                    (j = a.lastIndexOf(o, h)) !== q;) h.splice(j, 1);
                q += 1
            }
            d && h.reverse();
            return h
        }, inArray: function (b, d) {
            return -1 < a.indexOf(b, d)
        }, filter: d ? function (a, b, h) {
            return d.call(a, b, h || this)
        } : function (b, d, h) {
            var q = [];
            a.each(b, function (a, b, c) {
                d.call(h || this, a, b, c) && q.push(a)
            });
            return q
        }, map: b ? function (a, d, h) {
            return b.call(a, d, h || this)
        } : function (a, b, d) {
            for (var q =
                a.length, j = Array(q), o = 0; o < q; o++) {
                var m = "string" == typeof a ? a.charAt(o) : a[o];
                if (m || o in a) j[o] = b.call(d || this, m, o, a)
            }
            return j
        }, reduce: function (a, b, d) {
            var q = a.length;
            if ("function" !== typeof b) throw new TypeError("callback is not function!");
            if (0 === q && 2 == arguments.length) throw new TypeError("arguments invalid");
            var j = 0,
                o;
            if (3 <= arguments.length) o = arguments[2];
            else {
                do {
                    if (j in a) {
                        o = a[j++];
                        break
                    }
                    j += 1;
                    if (j >= q) throw new TypeError;
                } while (1)
            }
            for (; j < q;) j in a && (o = b.call(e, o, a[j], j, a)), j++;
            return o
        }, every: f ? function (a,
            b, d) {
            return f.call(a, b, d || this)
        } : function (a, b, d) {
            for (var q = a && a.length || 0, j = 0; j < q; j++)
                if (j in a && !b.call(d, a[j], j, a)) return !1;
            return !0
        }, some: n ? function (a, b, d) {
            return n.call(a, b, d || this)
        } : function (a, b, d) {
            for (var q = a && a.length || 0, j = 0; j < q; j++)
                if (j in a && b.call(d, a[j], j, a)) return !0;
            return !1
        }, makeArray: function (b) {
            if (null == b) return [];
            if (a.isArray(b)) return b;
            var d = typeof b.length,
                h = typeof b;
            if ("number" != d || b.alert || "string" == h || "function" == h && !("item" in b && "number" == d)) return [b];
            for (var d = [], h = 0, q = b.length; h <
                q; h++) d[h] = b[h];
            return d
        }
    })
})(KISSY);
(function (a, e) {
    function g(a) {
        var b = typeof a;
        return null == a || "object" !== b && "function" !== b
    }

    function l() {
        if (b) return b;
        var c = d;
        a.each(f, function (a) {
            c += a + "|"
        });
        c = c.slice(0, -1);
        return b = RegExp(c, "g")
    }

    function k() {
        if (c) return c;
        var b = d;
        a.each(n, function (a) {
            b += a + "|"
        });
        b += "&#(\\d{1,5});";
        return c = RegExp(b, "g")
    }
    var d = "",
        f = {
            "&amp;": "&",
            "&gt;": ">",
            "&lt;": "<",
            "&#x60;": "`",
            "&#x2F;": "/",
            "&quot;": '"',
            "&#x27;": "'"
        },
        n = {},
        b, c, p = /[\-#$\^*()+\[\]{}|\\,.?\s]/g;
    (function () {
        for (var a in f) n[f[a]] = a
    })();
    a.mix(a, {
        urlEncode: function (a) {
            return encodeURIComponent("" +
                a)
        }, urlDecode: function (a) {
            return decodeURIComponent(a.replace(/\+/g, " "))
        }, fromUnicode: function (a) {
            return a.replace(/\\u([a-f\d]{4})/ig, function (a, b) {
                return String.fromCharCode(parseInt(b, 16))
            })
        }, escapeHtml: function (a) {
            return (a + "").replace(l(), function (a) {
                return n[a]
            })
        }, escapeRegExp: function (a) {
            return a.replace(p, "\\$&")
        }, unEscapeHtml: function (a) {
            return a.replace(k(), function (a, b) {
                return f[a] || String.fromCharCode(+b)
            })
        }, param: function (b, c, j, o) {
            c = c || "&";
            j = j || "=";
            o === e && (o = !0);
            var m = [],
                i, r, f, p, k, n =
                a.urlEncode;
            for (i in b)
                if (k = b[i], i = n(i), g(k)) m.push(i), k !== e && m.push(j, n(k + d)), m.push(c);
                else if (a.isArray(k) && k.length) {
                r = 0;
                for (p = k.length; r < p; ++r) f = k[r], g(f) && (m.push(i, o ? n("[]") : d), f !== e && m.push(j, n(f + d)), m.push(c))
            }
            m.pop();
            return m.join(d)
        }, unparam: function (b, c, j) {
            if ("string" != typeof b || !(b = a.trim(b))) return {};
            for (var j = j || "=", d = {}, m, i = a.urlDecode, b = b.split(c || "&"), r = 0, f = b.length; r < f; ++r) {
                m = b[r].indexOf(j);
                if (-1 == m) c = i(b[r]), m = e;
                else {
                    c = i(b[r].substring(0, m));
                    m = b[r].substring(m + 1);
                    try {
                        m = i(m)
                    } catch (p) {
                        "decodeURIComponent error : " +
                        m, p
                    }
                    a.endsWith(c, "[]") && (c = c.substring(0, c.length - 2))
                }
                c in d ? a.isArray(d[c]) ? d[c].push(m) : d[c] = [d[c], m] : d[c] = m
            }
            return d
        }
    });
    a.escapeHTML = a.escapeHtml;
    a.unEscapeHTML = a.unEscapeHtml
})(KISSY);
(function (a) {
    function e(a, e, k) {
        var d = [].slice,
            f = d.call(arguments, 3),
            n = function () {},
            b = function () {
                var b = d.call(arguments);
                return e.apply(this instanceof n ? this : k, a ? b.concat(f) : f.concat(b))
            };
        n.prototype = e.prototype;
        b.prototype = new n;
        return b
    }
    a.mix(a, {
        noop: function () {}, bind: e(0, e, null, 0),
        rbind: e(0, e, null, 1),
        later: function (e, l, k, d, f) {
            var l = l || 0,
                n = e,
                b = a.makeArray(f),
                c;
            "string" == typeof e && (n = d[e]);
            n || "method undefined";
            e = function () {
                n.apply(d, b)
            };
            c = k ? setInterval(e, l) : setTimeout(e, l);
            return {
                id: c,
                interval: k,
                cancel: function () {
                    this.interval ? clearInterval(c) : clearTimeout(c)
                }
            }
        }, throttle: function (e, l, k) {
            l = l || 150;
            if (-1 === l) return function () {
                e.apply(k || this, arguments)
            };
            var d = a.now();
            return function () {
                var f = a.now();
                f - d > l && (d = f, e.apply(k || this, arguments))
            }
        }, buffer: function (e, l, k) {
            function d() {
                d.stop();
                f = a.later(e, l, 0, k || this, arguments)
            }
            l = l || 150;
            if (-1 === l) return function () {
                e.apply(k || this, arguments)
            };
            var f = null;
            d.stop = function () {
                f && (f.cancel(), f = 0)
            };
            return d
        }
    })
})(KISSY);
(function (a, e) {
    function g(b, c, e) {
        var h = b,
            q, j, o, m;
        if (!b) return h;
        if (b[f]) return e[b[f]].destination;
        if ("object" === typeof b) {
            m = b.constructor;
            if (a.inArray(m, [Boolean, String, Number, Date, RegExp])) h = new m(b.valueOf());
            else if (q = a.isArray(b)) h = c ? a.filter(b, c) : b.concat();
            else if (j = a.isPlainObject(b)) h = {};
            b[f] = m = a.guid("c");
            e[m] = {
                destination: h,
                input: b
            }
        }
        if (q)
            for (b = 0; b < h.length; b++) h[b] = g(h[b], c, e);
        else if (j)
            for (o in b)
                if (o !== f && (!c || c.call(b, b[o], o, b) !== d)) h[o] = g(b[o], c, e);
        return h
    }

    function l(b, c, d, h) {
        if (b[n] ===
            c && c[n] === b) return k;
        b[n] = c;
        c[n] = b;
        var f = function (a, b) {
                return null !== a && a !== e && a[b] !== e
            },
            j;
        for (j in c)!f(b, j) && f(c, j) && d.push("expected has key '" + j + "', but missing from actual.");
        for (j in b)!f(c, j) && f(b, j) && d.push("expected missing key '" + j + "', but present in actual.");
        for (j in c) j != n && (a.equals(b[j], c[j], d, h) || h.push("'" + j + "' was '" + (c[j] ? c[j].toString() : c[j]) + "' in expected, but was '" + (b[j] ? b[j].toString() : b[j]) + "' in actual."));
        a.isArray(b) && a.isArray(c) && b.length != c.length && h.push("arrays were not the same length");
        delete b[n];
        delete c[n];
        return 0 === d.length && 0 === h.length
    }
    var k = !0,
        d = !1,
        f = "__~ks_cloned",
        n = "__~ks_compared";
    a.mix(a, {
        equals: function (a, c, d, h) {
            d = d || [];
            h = h || [];
            return a === c ? k : a === e || null === a || c === e || null === c ? null == a && null == c : a instanceof Date && c instanceof Date ? a.getTime() == c.getTime() : "string" == typeof a && "string" == typeof c || "number" === typeof a && "number" === typeof c ? a == c : "object" === typeof a && "object" === typeof c ? l(a, c, d, h) : a === c
        }, clone: function (b, c) {
            var d = {},
                h = g(b, c, d);
            a.each(d, function (a) {
                a = a.input;
                if (a[f]) try {
                    delete a[f]
                } catch (b) {
                    a[f] = e
                }
            });
            d = null;
            return h
        }, now: Date.now || function () {
            return +new Date
        }
    })
})(KISSY);
(function (a, e) {
    var g = /^[\s\xa0]+|[\s\xa0]+$/g,
        l = String.prototype.trim,
        k = /\\?\{([^{}]+)\}/g;
    a.mix(a, {
        trim: l ? function (a) {
            return null == a ? "" : l.call(a)
        } : function (a) {
            return null == a ? "" : (a + "").replace(g, "")
        }, substitute: function (a, f, n) {
            return "string" != typeof a || !f ? a : a.replace(n || k, function (a, c) {
                return "\\" === a.charAt(0) ? a.slice(1) : f[c] === e ? "" : f[c]
            })
        }, ucfirst: function (a) {
            a += "";
            return a.charAt(0).toUpperCase() + a.substring(1)
        }, startsWith: function (a, e) {
            return 0 === a.lastIndexOf(e, 0)
        }, endsWith: function (a, e) {
            var k =
                a.length - e.length;
            return 0 <= k && a.indexOf(e, k) == k
        }
    })
})(KISSY);
(function (a, e) {
    var g = {},
        l = Object.prototype,
        k = l.toString;
    a.mix(a, {
        type: function (a) {
            return null == a ? "" + a : g[k.call(a)] || "object"
        }, isNull: function (a) {
            return null === a
        }, isUndefined: function (a) {
            return a === e
        }, isEmptyObject: function (a) {
            for (var f in a)
                if (f !== e) return !1;
            return !0
        }, isPlainObject: function (d) {
            if (!d || "object" !== a.type(d) || d.nodeType || d.window == d) return !1;
            var f, k;
            try {
                if ((k = d.constructor) && !l.hasOwnProperty.call(d, "constructor") && !l.hasOwnProperty.call(k.prototype, "isPrototypeOf")) return !1
            } catch (b) {
                return !1
            }
            for (f in d);
            return f === e || l.hasOwnProperty.call(d, f)
        }
    });
    a.each("Boolean,Number,String,Function,Date,RegExp,Object,Array".split(","), function (d, e) {
        g["[object " + d + "]"] = e = d.toLowerCase();
        a["is" + d] = function (d) {
            return a.type(d) == e
        }
    });
    a.isArray = Array.isArray || a.isArray
})(KISSY);
(function (a) {
    function e() {
        for (var a = 0, b; b = g[a++];) try {
            b()
        } catch (c) {
            setTimeout(function () {
                throw c;
            }, 0)
        }
        1 < a && (g = []);
        l = 0
    }
    var g = [],
        l = 0;
    a.setImmediate = function (a) {
        g.push(a);
        l || (l = 1, k())
    };
    var k;
    if ("function" === typeof setImmediate) k = function () {
        setImmediate(e)
    };
    else if ("undefined" !== typeof process && "function" == typeof process.nextTick) k = function () {
        process.nextTick(e)
    };
    else if ("undefined" !== typeof MessageChannel) {
        var d = new MessageChannel;
        d.port1.onmessage = function () {
            k = f;
            d.port1.onmessage = e;
            e()
        };
        var f = function () {
            d.port2.postMessage(0)
        };
        k = function () {
            setTimeout(e, 0);
            f()
        }
    } else k = function () {
        setTimeout(e, 0)
    }
})(KISSY);
(function (a) {
    function e(a, d) {
        for (var e = 0, g = a.length - 1, b = [], c; 0 <= g; g--) c = a[g], "." != c && (".." === c ? e++ : e ? e-- : b[b.length] = c);
        if (d)
            for (; e--; e) b[b.length] = "..";
        return b = b.reverse()
    }
    var g = /^(\/?)([\s\S]+\/(?!$)|\/)?((?:\.{1,2}$|[\s\S]+?)?(\.[^.\/]*)?)$/,
        l = a.Path = {
            resolve: function () {
                var k = "",
                    d, f = arguments,
                    g, b = 0;
                for (d = f.length - 1; 0 <= d && !b; d--) g = f[d], "string" == typeof g && g && (k = g + "/" + k, b = "/" == g.charAt(0));
                k = e(a.filter(k.split("/"), function (a) {
                    return !!a
                }), !b).join("/");
                return (b ? "/" : "") + k || "."
            }, normalize: function (k) {
                var d =
                    "/" == k.charAt(0),
                    f = "/" == k.slice(-1),
                    k = e(a.filter(k.split("/"), function (a) {
                        return !!a
                    }), !d).join("/");
                !k && !d && (k = ".");
                k && f && (k += "/");
                return (d ? "/" : "") + k
            }, join: function () {
                var e = a.makeArray(arguments);
                return l.normalize(a.filter(e, function (a) {
                    return a && "string" == typeof a
                }).join("/"))
            }, relative: function (e, d) {
                var e = l.normalize(e),
                    d = l.normalize(d),
                    f = a.filter(e.split("/"), function (a) {
                        return !!a
                    }),
                    g = [],
                    b, c, p = a.filter(d.split("/"), function (a) {
                        return !!a
                    });
                c = Math.min(f.length, p.length);
                for (b = 0; b < c && f[b] == p[b]; b++);
                for (c = b; b < f.length;) g.push(".."), b++;
                g = g.concat(p.slice(c));
                return g = g.join("/")
            }, basename: function (a, d) {
                var e;
                e = (a.match(g) || [])[3] || "";
                d && e && e.slice(-1 * d.length) == d && (e = e.slice(0, -1 * d.length));
                return e
            }, dirname: function (a) {
                var d = a.match(g) || [],
                    a = d[1] || "",
                    d = d[2] || "";
                if (!a && !d) return ".";
                d && (d = d.substring(0, d.length - 1));
                return a + d
            }, extname: function (a) {
                return (a.match(g) || [])[4] || ""
            }
        }
})(KISSY);
(function (a, e) {
    function g(b) {
        b._queryMap || (b._queryMap = a.unparam(b._query))
    }

    function l(a) {
        this._query = a || ""
    }

    function k(a, b) {
        return encodeURI(a).replace(b, function (a) {
            a = a.charCodeAt(0).toString(16);
            return "%" + (1 == a.length ? "0" + a : a)
        })
    }

    function d(b) {
        if (b instanceof d) return b.clone();
        var c = this;
        a.mix(c, {
            scheme: "",
            userInfo: "",
            hostname: "",
            port: "",
            path: "",
            query: "",
            fragment: ""
        });
        b = d.getComponents(b);
        a.each(b, function (b, d) {
            b = b || "";
            if ("query" == d) c.query = new l(b);
            else {
                try {
                    b = a.urlDecode(b)
                } catch (j) {
                    j + "urlDecode error : " +
                        b
                }
                c[d] = b
            }
        });
        return c
    }
    var f = /[#\/\?@]/g,
        n = /[#\?]/g,
        b = /[#@]/g,
        c = /#/g,
        p = RegExp("^(?:([\\w\\d+.-]+):)?(?://(?:([^/?#@]*)@)?([\\w\\d\\-\\u0100-\\uffff.+%]*|\\[[^\\]]+\\])(?::([0-9]+))?)?([^?#]+)?(?:\\?([^#]*))?(?:#(.*))?$"),
        h = a.Path,
        q = {
            scheme: 1,
            userInfo: 2,
            hostname: 3,
            port: 4,
            path: 5,
            query: 6,
            fragment: 7
        };
    l.prototype = {
        constructor: l,
        clone: function () {
            return new l(this.toString())
        }, reset: function (a) {
            this._query = a || "";
            this._queryMap = null;
            return this
        }, count: function () {
            var b = 0,
                c, d;
            g(this);
            c = this._queryMap;
            for (d in c) a.isArray(c[d]) ?
                b += c[d].length : b++;
            return b
        }, has: function (b) {
            var c;
            g(this);
            c = this._queryMap;
            return b ? b in c : !a.isEmptyObject(c)
        }, get: function (a) {
            var b;
            g(this);
            b = this._queryMap;
            return a ? b[a] : b
        }, keys: function () {
            g(this);
            return a.keys(this._queryMap)
        }, set: function (b, c) {
            var d;
            g(this);
            d = this._queryMap;
            "string" == typeof b ? this._queryMap[b] = c : (b instanceof l && (b = b.get()), a.each(b, function (a, b) {
                d[b] = a
            }));
            return this
        }, remove: function (a) {
            g(this);
            a ? delete this._queryMap[a] : this._queryMap = {};
            return this
        }, add: function (a, b) {
            var c,
                d;
            if ("string" == typeof a) g(this), c = this._queryMap, d = c[a], d = d === e ? b : [].concat(d).concat(b), c[a] = d;
            else
                for (c in a instanceof l && (a = a.get()), a) this.add(c, a[c]);
            return this
        }, toString: function (b) {
            g(this);
            return a.param(this._queryMap, e, e, b)
        }
    };
    d.prototype = {
        constructor: d,
        clone: function () {
            var b = new d,
                c = this;
            a.each(q, function (a, d) {
                b[d] = c[d]
            });
            b.query = b.query.clone();
            return b
        }, resolve: function (b) {
            "string" == typeof b && (b = new d(b));
            var c = 0,
                m, i = this.clone();
            a.each("scheme,userInfo,hostname,port,path,query,fragment".split(","),
                function (d) {
                    if (d == "path")
                        if (c) i[d] = b[d];
                        else {
                            if (d = b.path) {
                                c = 1;
                                if (!a.startsWith(d, "/"))
                                    if (i.hostname && !i.path) d = "/" + d;
                                    else if (i.path) {
                                    m = i.path.lastIndexOf("/");
                                    m != -1 && (d = i.path.slice(0, m + 1) + d)
                                }
                                i.path = h.normalize(d)
                            }
                        } else if (d == "query") {
                        if (c || b.query.toString()) {
                            i.query = b.query.clone();
                            c = 1
                        }
                    } else if (c || b[d]) {
                        i[d] = b[d];
                        c = 1
                    }
                });
            return i
        }, getScheme: function () {
            return this.scheme
        }, setScheme: function (a) {
            this.scheme = a;
            return this
        }, getHostname: function () {
            return this.hostname
        }, setHostname: function (a) {
            this.hostname =
                a;
            return this
        }, setUserInfo: function (a) {
            this.userInfo = a;
            return this
        }, getUserInfo: function () {
            return this.userInfo
        }, setPort: function (a) {
            this.port = a;
            return this
        }, getPort: function () {
            return this.port
        }, setPath: function (a) {
            this.path = a;
            return this
        }, getPath: function () {
            return this.path
        }, setQuery: function (c) {
            "string" == typeof c && (a.startsWith(c, "?") && (c = c.slice(1)), c = new l(k(c, b)));
            this.query = c;
            return this
        }, getQuery: function () {
            return this.query
        }, getFragment: function () {
            return this.fragment
        }, setFragment: function (b) {
            a.startsWith(b,
                "#") && (b = b.slice(1));
            this.fragment = b;
            return this
        }, isSameOriginAs: function (a) {
            return this.hostname.toLowerCase() == a.hostname.toLowerCase() && this.scheme.toLowerCase() == a.scheme.toLowerCase() && this.port.toLowerCase() == a.port.toLowerCase()
        }, toString: function (b) {
            var d = [],
                m, i;
            if (m = this.scheme) d.push(k(m, f)), d.push(":");
            if (m = this.hostname) {
                d.push("//");
                if (i = this.userInfo) d.push(k(i, f)), d.push("@");
                d.push(encodeURIComponent(m));
                if (i = this.port) d.push(":"), d.push(i)
            }
            if (i = this.path) m && !a.startsWith(i, "/") &&
                (i = "/" + i), i = h.normalize(i), d.push(k(i, n));
            if (b = this.query.toString.call(this.query, b)) d.push("?"), d.push(b);
            if (b = this.fragment) d.push("#"), d.push(k(b, c));
            return d.join("")
        }
    };
    d.Query = l;
    d.getComponents = function (b) {
        var c = (b || "").match(p) || [],
            d = {};
        a.each(q, function (a, b) {
            d[b] = c[a]
        });
        return d
    };
    a.Uri = d
})(KISSY);
(function (a, e) {
    function g(a) {
        var b = 0;
        return parseFloat(a.replace(/\./g, function () {
            return 0 === b++ ? "." : ""
        }))
    }

    function l(a, b) {
        var c;
        b.trident = 0.1;
        if ((c = a.match(/Trident\/([\d.]*)/)) && c[1]) b.trident = g(c[1]);
        b.core = "trident"
    }

    function k(a) {
        var b, c;
        return (b = a.match(/MSIE ([^;]*)|Trident.*; rv(?:\s|:)?([0-9.]+)/)) && (c = b[1] || b[2]) ? g(c) : 0
    }

    function d(a) {
        var b, c = "",
            d = "",
            i, h = [6, 9],
            f, p = n && n.createElement("div"),
            D = [],
            s = {
                webkit: e,
                trident: e,
                gecko: e,
                presto: e,
                chrome: e,
                safari: e,
                firefox: e,
                ie: e,
                opera: e,
                mobile: e,
                core: e,
                shell: e,
                phantomjs: e,
                os: e,
                ipad: e,
                iphone: e,
                ipod: e,
                ios: e,
                android: e,
                nodejs: e
            };
        p && p.getElementsByTagName && (p.innerHTML = "<\!--[if IE {{version}}]><s></s><![endif]--\>".replace("{{version}}", ""), D = p.getElementsByTagName("s"));
        if (0 < D.length) {
            l(a, s);
            i = h[0];
            for (h = h[1]; i <= h; i++)
                if (p.innerHTML = "<\!--[if IE {{version}}]><s></s><![endif]--\>".replace("{{version}}", i), 0 < D.length) {
                    s[d = "ie"] = i;
                    break
                }
            if (!s.ie && (f = k(a))) s[d = "ie"] = f
        } else if ((i = a.match(/AppleWebKit\/([\d.]*)/)) && i[1]) {
            s[c = "webkit"] = g(i[1]);
            if ((i = a.match(/OPR\/(\d+\.\d+)/)) &&
                i[1]) s[d = "opera"] = g(i[1]);
            else if ((i = a.match(/Chrome\/([\d.]*)/)) && i[1]) s[d = "chrome"] = g(i[1]);
            else if ((i = a.match(/\/([\d.]*) Safari/)) && i[1]) s[d = "safari"] = g(i[1]);
            if (/ Mobile\//.test(a) && a.match(/iPad|iPod|iPhone/)) {
                s.mobile = "apple";
                if ((i = a.match(/OS ([^\s]*)/)) && i[1]) s.ios = g(i[1].replace("_", "."));
                b = "ios";
                if ((i = a.match(/iPad|iPod|iPhone/)) && i[0]) s[i[0].toLowerCase()] = s.ios
            } else if (/ Android/i.test(a)) {
                if (/Mobile/.test(a) && (b = s.mobile = "android"), (i = a.match(/Android ([^\s]*);/)) && i[1]) s.android = g(i[1])
            } else if (i =
                a.match(/NokiaN[^\/]*|Android \d\.\d|webOS\/\d\.\d/)) s.mobile = i[0].toLowerCase();
            if ((i = a.match(/PhantomJS\/([^\s]*)/)) && i[1]) s.phantomjs = g(i[1])
        } else if ((i = a.match(/Presto\/([\d.]*)/)) && i[1]) {
            if (s[c = "presto"] = g(i[1]), (i = a.match(/Opera\/([\d.]*)/)) && i[1]) {
                s[d = "opera"] = g(i[1]);
                if ((i = a.match(/Opera\/.* Version\/([\d.]*)/)) && i[1]) s[d] = g(i[1]);
                if ((i = a.match(/Opera Mini[^;]*/)) && i) s.mobile = i[0].toLowerCase();
                else if ((i = a.match(/Opera Mobi[^;]*/)) && i) s.mobile = i[0]
            }
        } else if (f = k(a)) s[d = "ie"] = f, l(a, s);
        else if (i =
            a.match(/Gecko/)) {
            s[c = "gecko"] = 0.1;
            if ((i = a.match(/rv:([\d.]*)/)) && i[1]) s[c] = g(i[1]), /Mobile|Tablet/.test(a) && (s.mobile = "firefox");
            if ((i = a.match(/Firefox\/([\d.]*)/)) && i[1]) s[d = "firefox"] = g(i[1])
        }
        b || (/windows|win32/i.test(a) ? b = "windows" : /macintosh|mac_powerpc/i.test(a) ? b = "macintosh" : /linux/i.test(a) ? b = "linux" : /rhino/i.test(a) && (b = "rhino"));
        s.os = b;
        s.core = s.core || c;
        s.shell = d;
        return s
    }
    var f = a.Env.host,
        n = f.document,
        f = f.navigator,
        b = KISSY.UA = d(f && f.userAgent || "");
    if ("object" === typeof process) {
        var c, p;
        if ((c =
            process.versions) && (p = c.node)) b.os = process.platform, b.nodejs = g(p)
    }
    b.getDescriptorFromUserAgent = d;
    c = "webkit,trident,gecko,presto,chrome,safari,firefox,ie,opera".split(",");
    p = n && n.documentElement;
    var h = "";
    p && (a.each(c, function (a) {
        var c = b[a];
        c && (h += " ks-" + a + (parseInt(c) + ""), h += " ks-" + a)
    }), a.trim(h) && (p.className = a.trim(p.className + h)))
})(KISSY);
(function (a, e) {
    var g = a.Env.host,
        l = a.UA,
        k = ["", "Webkit", "Moz", "O", "ms"],
        d = g.document || {},
        f, n, b, c, p, h = d.documentElement,
        q, j = !0,
        o = !1,
        m = "ontouchstart" in d && !l.phantomjs,
        i = d.documentMode || l.ie;
    h && (h.querySelector && 8 != i && (o = !0), q = h.style, a.each(k, function (a) {
        var d = a ? a + "Transition" : "transition",
            i = a ? a + "Transform" : "transform";
        c === e && d in q && (c = a, n = d);
        p === e && i in q && (p = a, b = i)
    }), j = "classList" in h, f = "msPointerEnabled" in (g.navigator || {}));
    a.Features = {
        isMsPointerSupported: function () {
                return f
            }, isTouchEventSupported: function () {
                return m
            },
            isDeviceMotionSupported: function () {
                return !!g.DeviceMotionEvent
            }, isHashChangeSupported: function () {
                return "onhashchange" in g && (!i || i > 7)
            }, isTransitionSupported: function () {
                return c !== e
            }, isTransformSupported: function () {
                return p !== e
            }, isClassListSupported: function () {
                return j
            }, isQuerySelectorSupported: function () {
                return !a.config("dom/selector") && o
            }, isIELessThan: function (a) {
                return !!(i && i < a)
            }, getTransitionPrefix: function () {
                return c
            }, getTransformPrefix: function () {
                return p
            }, getTransitionProperty: function () {
                return n
            },
            getTransformProperty: function () {
                return b
            }
    }
})(KISSY);
(function (a) {
    (a.Loader = {}).Status = {
        INIT: 0,
        LOADING: 1,
        LOADED: 2,
        ERROR: 3,
        ATTACHED: 4
    }
})(KISSY);
(function (a) {
    function e(a) {
        if ("string" == typeof a) return g(a);
        for (var b = [], c = 0, d = a.length; c < d; c++) b[c] = g(a[c]);
        return b
    }

    function g(a) {
        "/" == a.charAt(a.length - 1) && (a += "index");
        return a
    }

    function l(b, c) {
        var d = c.indexOf("!");
        if (-1 != d) {
            var e = c.substring(0, d),
                c = c.substring(d + 1);
            a.use(e, {
                sync: !0,
                success: function (a, d) {
                    d.alias && (c = d.alias(b, c, e))
                }
            })
        }
        return c
    }
    var k = a.Loader,
        d = a.Path,
        f = a.Env.host,
        n = a.startsWith,
        b = k.Status,
        c = b.ATTACHED,
        p = b.LOADED,
        h = b.ERROR,
        q = k.Utils = {},
        j = f.document;
    a.mix(q, {
        docHead: function () {
            return j.getElementsByTagName("head")[0] ||
                j.documentElement
        }, normalDepModuleName: function (a, b) {
            var c = 0,
                e;
            if (!b) return b;
            if ("string" == typeof b) return n(b, "../") || n(b, "./") ? d.resolve(d.dirname(a), b) : d.normalize(b);
            for (e = b.length; c < e; c++) b[c] = q.normalDepModuleName(a, b[c]);
            return b
        }, createModulesInfo: function (b, c) {
            a.each(c, function (a) {
                q.createModuleInfo(b, a)
            })
        }, createModuleInfo: function (b, c, d) {
            var c = g(c),
                e = b.Env.mods,
                h = e[c];
            return h ? h : e[c] = h = new k.Module(a.mix({
                name: c,
                runtime: b
            }, d))
        }, isAttached: function (b, d) {
            var e;
            a: {
                var h = b.Env.mods,
                    f, g;
                e = a.makeArray(d);
                for (g = 0; g < e.length; g++)
                    if (f = h[e[g]], !f || f.status !== c) {
                        e = 0;
                        break a
                    }
                e = 1
            }
            return e
        }, getModules: function (b, d) {
            var e = [b],
                h, f, g, p, j = b.Env.mods;
            a.each(d, function (d) {
                h = j[d];
                if (!h || "css" != h.getType()) f = q.unalias(b, d), (g = a.reduce(f, function (a, b) {
                    p = j[b];
                    return a && p && p.status == c
                }, !0)) ? e.push(j[f[0]].value) : e.push(null)
            });
            return e
        }, attachModsRecursively: function (a, b, c, d, e) {
            var c = c || [],
                e = e || {},
                h, f = 1,
                g = a.length,
                p = c.length;
            for (h = 0; h < g; h++) f = f && q.attachModRecursively(a[h], b, c, d, e), c.length = p;
            return f
        }, attachModRecursively: function (a,
            b, d, e, f) {
            var g, j = b.Env.mods[a];
            if (a in f) return f[a];
            if (!j) return f[a] = 0;
            g = j.status;
            if (g == c) return f[a] = 1;
            g == h && e.push(j);
            return g != p ? f[a] = 0 : q.attachModsRecursively(j.getNormalizedRequires(), b, d, e, f) ? (q.attachMod(b, j), f[a] = 1) : f[a] = 0
        }, attachMod: function (a, b) {
            if (b.status == p) {
                var d = b.fn;
                b.value = "function" === typeof d ? d.apply(b, q.getModules(a, b.getRequiresWithAlias())) : d;
                b.status = c
            }
        }, getModNamesAsArray: function (a) {
            "string" == typeof a && (a = a.replace(/\s+/g, "").split(","));
            return a
        }, normalizeModNames: function (a,
            b, c) {
            return q.unalias(a, q.normalizeModNamesWithAlias(a, b, c))
        }, unalias: function (a, b) {
            for (var c = [].concat(b), d, h, f, g = 0, j, p = a.Env.mods; !g;) {
                g = 1;
                for (d = c.length - 1; 0 <= d; d--)
                    if ((h = p[c[d]]) && (f = h.alias)) {
                        g = 0;
                        for (j = f.length - 1; 0 <= j; j--) f[j] || f.splice(j, 1);
                        c.splice.apply(c, [d, 1].concat(e(f)))
                    }
            }
            return c
        }, normalizeModNamesWithAlias: function (a, b, c) {
            var d = [],
                h, f;
            if (b) {
                h = 0;
                for (f = b.length; h < f; h++) b[h] && d.push(l(a, e(b[h])))
            }
            c && (d = q.normalDepModuleName(c, d));
            return d
        }, registerModule: function (b, c, d, e) {
            var c = g(c),
                h = b.Env.mods,
                f = h[c];
            f && f.fn ? (c + " is defined more than once", "warn") : (q.createModuleInfo(b, c), f = h[c], a.mix(f, {
                name: c,
                status: p,
                fn: d
            }), a.mix(f, e))
        }, getMappedPath: function (a, b, c) {
            for (var a = c || a.Config.mappedRules || [], d, c = 0; c < a.length; c++)
                if (d = a[c], b.match(d[0])) return b.replace(d[0], d[1]);
            return b
        }, getHash: function (a) {
            var b = 5381,
                c;
            for (c = a.length; - 1 < --c;) b = (b << 5) + b + a.charCodeAt(c);
            return b + ""
        }
    })
})(KISSY);
(function (a) {
    function e(a, c) {
        return c in a ? a[c] : a.runtime.Config[c]
    }

    function g(b) {
        a.mix(this, b)
    }

    function l(b) {
        this.status = k.Status.INIT;
        a.mix(this, b);
        this.waitedCallbacks = []
    }
    var k = a.Loader,
        d = a.Path,
        f = k.Utils;
    a.augment(g, {
        reset: function (b) {
            a.mix(this, b)
        }, getTag: function () {
            return e(this, "tag")
        }, getName: function () {
            return this.name
        }, getBase: function () {
            return e(this, "base")
        }, getPrefixUriForCombo: function () {
            var a = this.getName();
            return this.getBase() + (a && !this.isIgnorePackageNameInUri() ? a + "/" : "")
        }, getPackageUri: function () {
            return this.packageUri ?
                this.packageUri : this.packageUri = new a.Uri(this.getPrefixUriForCombo())
        }, getBaseUri: function () {
            return e(this, "baseUri")
        }, isDebug: function () {
            return e(this, "debug")
        }, isIgnorePackageNameInUri: function () {
            return e(this, "ignorePackageNameInUri")
        }, getCharset: function () {
            return e(this, "charset")
        }, isCombine: function () {
            return e(this, "combine")
        }, getGroup: function () {
            return e(this, "group")
        }
    });
    k.Package = g;
    a.augment(l, {
        wait: function (a) {
                this.waitedCallbacks.push(a)
            }, notifyAll: function () {
                for (var a, c = this.waitedCallbacks.length,
                    d = 0; d < c; d++) {
                    a = this.waitedCallbacks[d];
                    try {
                        a(this)
                    } catch (e) {
                        e.stack || e, "error", setTimeout(function () {
                            throw e;
                        }, 0)
                    }
                }
                this.waitedCallbacks = []
            }, setValue: function (a) {
                this.value = a
            }, getType: function () {
                var a = this.type;
                a || (this.type = a = ".css" == d.extname(this.name).toLowerCase() ? "css" : "js");
                return a
            }, getFullPathUri: function () {
                var b, c, e, h;
                if (!this.fullPathUri) {
                    if (this.fullpath) c = new a.Uri(this.fullpath);
                    else {
                        c = this.getPackage();
                        b = c.getBaseUri();
                        h = this.getPath();
                        if (c.isIgnorePackageNameInUri() && (e = c.getName())) h =
                            d.relative(e, h);
                        c = b.resolve(h);
                        if (b = this.getTag()) b += "." + this.getType(), c.query.set("t", b)
                    }
                    this.fullPathUri = c
                }
                return this.fullPathUri
            }, getFullPath: function () {
                var a;
                this.fullpath || (a = this.getFullPathUri(), this.fullpath = f.getMappedPath(this.runtime, a.toString()));
                return this.fullpath
            }, getPath: function () {
                var a;
                if (!(a = this.path)) {
                    a = this.name;
                    var c = "." + this.getType(),
                        e = "-min";
                    a = d.join(d.dirname(a), d.basename(a, c));
                    this.getPackage().isDebug() && (e = "");
                    a = this.path = a + e + c
                }
                return a
            }, getValue: function () {
                return this.value
            },
            getName: function () {
                return this.name
            }, getPackage: function () {
                var b;
                if (!(b = this.packageInfo)) {
                    var c = this.name;
                    b = this.runtime.config("packages");
                    var c = c + "/",
                        d = "",
                        e;
                    for (e in b) a.startsWith(c, e + "/") && e.length > d.length && (d = e);
                    b = this.packageInfo = b[d] || n
                }
                return b
            }, getTag: function () {
                return this.tag || this.getPackage().getTag()
            }, getCharset: function () {
                return this.charset || this.getPackage().getCharset()
            }, getRequiredMods: function () {
                var b = this.runtime;
                return a.map(this.getNormalizedRequires(), function (a) {
                    return f.createModuleInfo(b,
                        a)
                })
            }, getRequiresWithAlias: function () {
                var a = this.requiresWithAlias,
                    c = this.requires;
                if (!c || 0 == c.length) return c || [];
                a || (this.requiresWithAlias = a = f.normalizeModNamesWithAlias(this.runtime, c, this.name));
                return a
            }, getNormalizedRequires: function () {
                var a, c = this.normalizedRequiresStatus,
                    d = this.status,
                    e = this.requires;
                if (!e || 0 == e.length) return e || [];
                if ((a = this.normalizedRequires) && c == d) return a;
                this.normalizedRequiresStatus = d;
                return this.normalizedRequires = f.normalizeModNames(this.runtime, e, this.name)
            }
    });
    k.Module = l;
    var n = new g({
        name: "",
        runtime: a
    })
})(KISSY);
(function (a) {
    function e(a, c) {
        var d = 0;
        if (k.webkit) a.sheet && ("webkit css poll loaded: " + c, d = 1);
        else if (a.sheet) try {
            a.sheet.cssRules && ("same domain css poll loaded: " + c, d = 1)
        } catch (e) {
            var f = e.name;
            "css poll exception: " + f + " " + e.code + " " + c;
            "NS_ERROR_DOM_SECURITY_ERR" == f && ("css poll exception: " + f + "loaded : " + c, d = 1)
        }
        return d
    }

    function g() {
        for (var b in n) {
            var c = n[b],
                d = c.node;
            e(d, b) && (c.callback && c.callback.call(d), delete n[b])
        }
        a.isEmptyObject(n) ? ("clear css poll timer", f = 0) : f = setTimeout(g, l)
    }
    var l = 30,
        k = a.UA,
        d = a.Loader.Utils,
        f = 0,
        n = {};
    d.pollCss = function (a, c) {
        var d;
        d = n[a.href] = {};
        d.node = a;
        d.callback = c;
        f || ("start css poll timer", g())
    };
    d.isCssLoaded = e
})(KISSY);
(function (a) {
    var e = a.Env.host.document,
        g = a.Loader.Utils,
        l = a.Path,
        k = {},
        d, f = a.UA;
    a.getScript = function (n, b, c) {
        function p() {
            var a = r.readyState;
            if (!a || "loaded" == a || "complete" == a) r.onreadystatechange = r.onload = null, A(0)
        }
        var h = b,
            q = 0,
            j, o, m, i;
        a.startsWith(l.extname(n).toLowerCase(), ".css") && (q = 1);
        a.isPlainObject(h) && (b = h.success, j = h.error, o = h.timeout, c = h.charset, m = h.attrs);
        h = k[n] = k[n] || [];
        h.push([b, j]);
        if (1 < h.length) return h.node;
        var r = e.createElement(q ? "link" : "script");
        m && a.each(m, function (a, b) {
            r.setAttribute(b,
                a)
        });
        c && (r.charset = c);
        q ? (r.href = n, r.rel = "stylesheet") : (r.src = n, r.async = !0);
        h.node = r;
        var A = function (b) {
                var c;
                if (i) {
                    i.cancel();
                    i = void 0
                }
                a.each(k[n], function (a) {
                    (c = a[b]) && c.call(r)
                });
                delete k[n]
            },
            b = "onload" in r,
            c = a.Config.forceCssPoll || f.webkit && 536 > f.webkit;
        q && c && b && (b = !1);
        b ? (r.onload = p, r.onerror = function () {
            r.onerror = null;
            A(1)
        }) : q ? g.pollCss(r, function () {
            A(0)
        }) : r.onreadystatechange = p;
        o && (i = a.later(function () {
            A(1)
        }, 1E3 * o));
        d || (d = g.docHead());
        q ? d.appendChild(r) : d.insertBefore(r, d.firstChild);
        return r
    }
})(KISSY);
(function (a, e) {
    function g(b) {
        b = b.replace(/\\/g, "/");
        "/" != b.charAt(b.length - 1) && (b += "/");
        f ? b = f.resolve(b) : (a.startsWith(b, "file:") || (b = "file:" + b), b = new a.Uri(b));
        return b
    }
    var l = a.Loader,
        k = l.Utils,
        d = a.Env.host.location,
        f, n, b = a.Config.fns;
    if (!a.UA.nodejs && d && (n = d.href)) f = new a.Uri(n);
    a.Config.loadModsFn = function (b, d) {
        a.getScript(b.fullpath, d)
    };
    b.map = function (a) {
        var b = this.Config;
        return !1 === a ? b.mappedRules = [] : b.mappedRules = (b.mappedRules || []).concat(a || [])
    };
    b.mapCombo = function (a) {
        var b = this.Config;
        return !1 ===
            a ? b.mappedComboRules = [] : b.mappedComboRules = (b.mappedComboRules || []).concat(a || [])
    };
    b.packages = function (b) {
        var d, f = this.Config,
            k = f.packages = f.packages || {};
        return b ? (a.each(b, function (b, c) {
            d = b.name || c;
            var e = g(b.base || b.path);
            b.name = d;
            b.base = e.toString();
            b.baseUri = e;
            b.runtime = a;
            delete b.path;
            k[d] ? k[d].reset(b) : k[d] = new l.Package(b)
        }), e) : !1 === b ? (f.packages = {}, e) : k
    };
    b.modules = function (b) {
        var d = this;
        b && a.each(b, function (b, c) {
            var e = k.createModuleInfo(d, c, b);
            e.status == l.Status.INIT && a.mix(e, b)
        })
    };
    b.base =
        function (a) {
            var b = this.Config;
            if (!a) return b.base;
            a = g(a);
            b.base = a.toString();
            b.baseUri = a;
            return e
        }
})(KISSY);
(function (a, e) {
    function g(c, f, h, g, j) {
        var k = f && f.length,
            l = [],
            o = [];
        a.each(f, function (f) {
            var n, p = {
                timeout: j,
                success: function () {
                    o.push(f);
                    n && m && ("standard browser get mod name after load : " + n.name, b.registerModule(c, n.name, m.fn, m.config), m = e);
                    --k || h(o, l)
                }, error: function () {
                    l.push(f);
                    --k || h(o, l)
                }, charset: g
            };
            f.combine || (n = f.mods[0], "css" == n.getType() ? n = e : d && (i = n.name, r = a.now(), p.attrs = {
                "data-mod-name": n.name
            }));
            a.Config.loadModsFn(f, p)
        })
    }

    function l(b, d) {
        a.mix(this, {
            runtime: b,
            waitingModules: d
        })
    }

    function k(a,
        b) {
        for (var a = a.split(/\//), b = b.split(/\//), d = Math.min(a.length, b.length), c = 0; c < d && a[c] === b[c]; c++);
        return a.slice(0, c).join("/") + "/"
    }
    var d = a.UA.ie && 10 > a.UA.ie,
        f = a.Loader,
        n = f.Status,
        b = f.Utils,
        c = b.getHash,
        p = n.LOADING,
        h = n.LOADED,
        q = n.ERROR,
        j = a.now(),
        o = n.ATTACHED;
    l.groupTag = j;
    var m, i, r;
    l.add = function (c, f, h, g) {
        if ("function" === typeof c)
            if (h = f, f = c, d) {
                var c = a.Env.host.document.getElementsByTagName("script"),
                    j, k, l;
                for (k = c.length - 1; 0 <= k; k--)
                    if (l = c[k], "interactive" == l.readyState) {
                        j = l;
                        break
                    }
                j ? j = j.getAttribute("data-mod-name") :
                    ("can not find interactive script,time diff : " + (a.now() - r), "old_ie get mod name from cache : " + i, j = i);
                b.registerModule(g, j, f, h);
                i = null;
                r = 0
            } else m = {
                fn: f,
                config: h
            };
        else d ? (i = null, r = 0) : m = e, b.registerModule(g, c, f, h)
    };
    a.augment(l, {
        use: function (c) {
            var d = a.Config.timeout,
                e = this.runtime,
                c = a.keys(this.calculate(c));
            b.createModulesInfo(e, c);
            c = this.getComboUrls(c);
            a.each(c.css, function (c) {
                g(e, c, function (c, d) {
                    a.each(c, function (c) {
                        a.each(c.mods, function (c) {
                            b.registerModule(e, c.getName(), a.noop);
                            c.notifyAll()
                        })
                    });
                    a.each(d, function (b) {
                        a.each(b.mods, function (a) {
                            a.name + " is not loaded! can not find module in path : " + b.fullpath;
                            "error";
                            a.status = q;
                            a.notifyAll()
                        })
                    })
                }, c.charset, d)
            });
            a.each(c.js, function (b) {
                g(e, b, function () {
                    a.each(b, function (b) {
                        a.each(b.mods, function (a) {
                            a.fn || (a.name + " is not loaded! can not find module in path : " + b.fullpath, "error", a.status = q);
                            a.notifyAll()
                        })
                    })
                }, b.charset, d)
            })
        }, calculate: function (a, c, d) {
            var e, f, g, j, i = this.waitingModules,
                k = this.runtime,
                d = d || {},
                c = c || {};
            for (e = 0; e < a.length; e++) f = a[e],
                c[f] || (c[f] = 1, g = b.createModuleInfo(k, f), j = g.status, j === q || j === o || (j != h && !i.contains(f) && (j != p && (g.status = p, d[f] = 1), g.wait(function (a) {
                    i.remove(a.getName());
                    i.notifyAll()
                }), i.add(f)), this.calculate(g.getNormalizedRequires(), c, d)));
            return d
        }, getComboMods: function (c, d) {
            for (var e = {}, f, h = this.runtime, g = 0, i = c.length, l, n, m, o, p, r, q, G, I; g < i; ++g) {
                l = c[g];
                l = b.createModuleInfo(h, l);
                m = l.getType();
                I = l.getFullPath();
                n = l.getPackage();
                q = n.getName();
                p = n.getCharset();
                o = n.getTag();
                G = n.getGroup();
                r = n.getPrefixUriForCombo();
                f = n.getPackageUri();
                var v = q;
                (l.canBeCombined = n.isCombine() && a.startsWith(I, r)) && G ? (v = G + "_" + p + "_" + j, (n = d[v]) ? n.isSameOriginAs(f) ? n.setPath(k(n.getPath(), f.getPath())) : (v = q, d[q] = f) : d[v] = f.clone()) : d[q] = f;
                f = e[m] = e[m] || {};
                (m = f[v]) ? 1 == m.tags.length && m.tags[0] == o || m.tags.push(o): (m = f[v] = [], m.charset = p, m.tags = [o]);
                m.push(l)
            }
            return e
        }, getComboUrls: function (a) {
            var d = this.runtime,
                e = d.Config,
                f = e.comboPrefix,
                h = e.comboSep,
                g = e.comboMaxFileNum,
                j = e.comboMaxUrlLength,
                i = {},
                a = this.getComboMods(a, i),
                k = {},
                l;
            for (l in a) {
                k[l] = {};
                for (var n in a[l]) {
                    var m = [],
                        o = [],
                        p = a[l][n],
                        q = p.tags,
                        r = (q = 1 < q.length ? c(q.join("")) : q[0]) ? "?t=" + encodeURIComponent(q) + "." + l : "",
                        q = r.length,
                        v = i[n].toString(),
                        M = v.length,
                        J = v + f,
                        y = k[l][n] = [],
                        v = J.length;
                    y.charset = p.charset;
                    y.mods = [];
                    for (var K = function () {
                        y.push({
                            combine: 1,
                            fullpath: b.getMappedPath(d, J + m.join(h) + r, e.mappedComboRules),
                            mods: o
                        })
                    }, B = 0; B < p.length; B++) {
                        var z = p[B];
                        y.mods.push(z);
                        var C = z.getFullPath();
                        if (z.canBeCombined) {
                            if (C = C.slice(M).replace(/\?.*$/, ""), m.push(C), o.push(z), m.length > g || v + m.join(h).length +
                                q > j) m.pop(), o.pop(), K(), m = [], o = [], B--
                        } else y.push({
                            combine: 0,
                            fullpath: C,
                            mods: [z]
                        })
                    }
                    m.length && K()
                }
            }
            return k
        }
    });
    f.ComboLoader = l
})(KISSY);
(function (a, e) {
    function g(b) {
        a.mix(this, {
            fn: b,
            waitMods: {}
        })
    }
    var l = a.Loader,
        k = a.Env,
        d = l.Utils,
        f = a.setImmediate,
        n = l.ComboLoader;
    g.prototype = {
        constructor: g,
        notifyAll: function () {
            var b = this.fn;
            b && a.isEmptyObject(this.waitMods) && (this.fn = null, b())
        }, add: function (a) {
            this.waitMods[a] = 1
        }, remove: function (a) {
            delete this.waitMods[a]
        }, contains: function (a) {
            return this.waitMods[a]
        }
    };
    l.WaitingModules = g;
    a.mix(a, {
        add: function (b, c, d) {
            n.add(b, c, d, a)
        }, use: function (b, c) {
            function k() {
                ++m;
                var g = [],
                    n = a.now(),
                    t;
                t = d.attachModsRecursively(h,
                    a, e, g);
                m + " check duration " + (a.now() - n);
                t ? c && (o ? c.apply(a, d.getModules(a, b)) : f(function () {
                    c.apply(a, d.getModules(a, b))
                })) : g.length ? j && (o ? j.apply(a, g) : f(function () {
                    j.apply(a, g)
                })) : (m + " reload " + b, i.fn = k, l.use(h))
            }
            var h, l, j, o, m = 0,
                i = new g(k);
            a.isPlainObject(c) && (o = c.sync, j = c.error, c = c.success);
            b = d.getModNamesAsArray(b);
            b = d.normalizeModNamesWithAlias(a, b);
            h = d.unalias(a, b);
            l = new n(a, i);
            o ? i.notifyAll() : f(function () {
                i.notifyAll()
            });
            return a
        }, require: function (b) {
            b = d.unalias(a, d.normalizeModNamesWithAlias(a, [b]));
            return d.attachModsRecursively(b, a) ? d.getModules(a, b)[1] : e
        }
    });
    k.mods = {}
})(KISSY);
(function (a) {
    function e(b) {
        var c = b.src || "";
        if (!c.match(n)) return 0;
        var b = (b = b.getAttribute("data-config")) ? (new Function("return " + b))() : {},
            e = b.comboPrefix = b.comboPrefix || "??",
            g = b.comboSep = b.comboSep || ",",
            l, j = c.indexOf(e); - 1 == j ? l = c.replace(f, "$1") : (l = c.substring(0, j), "/" != l.charAt(l.length - 1) && (l += "/"), e = c.substring(j + e.length).split(g), a.each(e, function (a) {
            if (a.match(n)) return l += a.replace(f, "$1"), !1
        }));
        return a.mix({
            base: l,
            tag: k.getHash(d + c)
        }, b)
    }

    function g() {
        var a = l.getElementsByTagName("script"),
            c, d;
        for (c = a.length - 1; 0 <= c; c--)
            if (d = e(a[c])) return d;
            "must load kissy by file name in browser environment: seed.js or seed-min.js";
            "error";
        return null
    }
    var l = a.Env.host && a.Env.host.document,
        k = a.Loader.Utils,
        d = "20131121234724",
        f = /^(.*)(seed|kissy)(?:-min)?\.js[^\/]*/i,
        n = /(seed|kissy)(?:-min)?\.js/i;
    a.config({
        charset: "utf-8",
        lang: "zh-cn",
        tag: d
    });
    a.UA.nodejs ? a.config({
        charset: "utf-8",
        base: __dirname.replace(/\\/g, "/").replace(/\/$/, "") + "/"
    }) : l && l.getElementsByTagName && a.config(a.mix({
        comboMaxUrlLength: 2E3,
        comboMaxFileNum: 40
    }, g()))
})(KISSY);
KISSY.add("i18n", {
    alias: function (a, e) {
        return e + "/i18n/" + a.Config.lang
    }
});
(function (a, e) {
    function g() {
        if (!b) {
            d && !k.nodejs && m(l, j, g);
            b = 1;
            for (var e = 0; e < c.length; e++) try {
                c[e](a)
            } catch (f) {
                f.stack || f, "error", setTimeout(function () {
                    throw f;
                }, 0)
            }
        }
    }
    var l = a.Env.host,
        k = a.UA,
        d = l.document,
        f = d && d.documentElement,
        n = l.location,
        b = 0,
        c = [],
        p = /^#?([\w-]+)$/,
        h = /\S/,
        q = !(!d || !d.addEventListener),
        j = "load",
        o = q ? function (a, b, c) {
            a.addEventListener(b, c, !1)
        } : function (a, b, c) {
            a.attachEvent("on" + b, c)
        },
        m = q ? function (a, b, c) {
            a.removeEventListener(b, c, !1)
        } : function (a, b, c) {
            a.detachEvent("on" + b, c)
        };
    a.mix(a, {
        isWindow: function (a) {
            return null !=
                a && a == a.window
        }, parseXML: function (a) {
            if (a.documentElement) return a;
            var b;
            try {
                l.DOMParser ? b = (new DOMParser).parseFromString(a, "text/xml") : (b = new ActiveXObject("Microsoft.XMLDOM"), b.async = !1, b.loadXML(a))
            } catch (c) {
                "parseXML error :", c, b = e
            }(!b || !b.documentElement || b.getElementsByTagName("parsererror").length) && "Invalid XML: " + a;
            return b
        }, globalEval: function (a) {
            a && h.test(a) && (l.execScript || function (a) {
                l.eval.call(l, a)
            })(a)
        }, ready: function (d) {
            if (b) try {
                d(a)
            } catch (e) {
                e.stack || e, "error", setTimeout(function () {
                    throw e;
                }, 0)
            } else c.push(d);
            return this
        }, available: function (b, c) {
            var b = (b + "").match(p)[1],
                e = 1,
                f = a.later(function () {
                    if (500 < ++e) f.cancel();
                    else {
                        var a = d.getElementById(b);
                        a && (c(a), f.cancel())
                    }
                }, 40, !0)
        }
    });
    if (n && -1 !== (n.search || "").indexOf("ks-debug")) a.Config.debug = !0;
    (function () {
        if (!d || "complete" === d.readyState) g();
        else if (o(l, j, g), q) {
            var a = function () {
                m(d, "DOMContentLoaded", a);
                g()
            };
            o(d, "DOMContentLoaded", a)
        } else {
            var b = function () {
                "complete" === d.readyState && (m(d, "readystatechange", b), g())
            };
            o(d, "readystatechange",
                b);
            var c, e = f && f.doScroll;
            try {
                c = null === l.frameElement
            } catch (h) {
                c = !1
            }
            if (e && c) {
                var i = function () {
                    try {
                        e("left"), g()
                    } catch (a) {
                        setTimeout(i, 40)
                    }
                };
                i()
            }
        }
    })();
    if (k.ie) try {
        d.execCommand("BackgroundImageCache", !1, !0)
    } catch (i) {}
})(KISSY, void 0);
(function (a) {
    a.config({
        modules: {
            core: {
                alias: "dom,event,io,anim,base,node,json,ua,cookie".split(",")
            },
            ajax: {
                alias: ["io"]
            },
            "rich-base": {
                alias: ["base"]
            }
        }
    });
    if ("undefined" != typeof location) {
        var e = a.startsWith(location.href, "https") ? "https://s.tbcdn.cn/s/kissy/" : "http://a.tbcdn.cn/s/kissy/";
        a.config({
            packages: {
                gallery: {
                    base: e
                },
                mobile: {
                    base: e
                }
            }
        })
    }
})(KISSY);
(function (a, e, g) {
    a({
        anim: {
            requires: ["dom", "anim/base", "anim/timer", KISSY.Features.isTransitionSupported() ? "anim/transition" : ""]
        }
    });
    a({
        "anim/base": {
            requires: ["dom", "promise"]
        }
    });
    a({
        "anim/timer": {
            requires: ["dom", "anim/base"]
        }
    });
    a({
        "anim/transition": {
            requires: ["dom", "event/dom", "anim/base"]
        }
    });
    a({
        base: {
            requires: ["event/custom"]
        }
    });
    a({
        button: {
            requires: ["node", "component/control"]
        }
    });
    a({
        color: {
            requires: ["base"]
        }
    });
    a({
        combobox: {
            requires: ["node", "component/control", "menu", "base", "io"]
        }
    });
    a({
        "component/container": {
            requires: ["component/control",
                "component/manager"
            ]
        }
    });
    a({
        "component/control": {
            requires: ["node", "base", "promise", "component/manager", "xtemplate/runtime"]
        }
    });
    a({
        "component/extension/align": {
            requires: ["node"]
        }
    });
    a({
        "component/extension/delegate-children": {
            requires: ["node", "component/manager"]
        }
    });
    a({
        "component/plugin/drag": {
            requires: ["base", "dd"]
        }
    });
    a({
        "component/plugin/resize": {
            requires: ["resizable"]
        }
    });
    a({
        "date/format": {
            requires: ["date/gregorian", "i18n!date"]
        }
    });
    a({
        "date/gregorian": {
            requires: ["i18n!date"]
        }
    });
    a({
        "date/picker": {
            requires: ["node",
                "date/gregorian", "i18n!date/picker", "component/control", "date/format"
            ]
        }
    });
    a({
        "date/popup-picker": {
            requires: ["date/picker", "component/extension/align", "component/extension/shim"]
        }
    });
    a({
        dd: {
            requires: ["node", "base"]
        }
    });
    a({
        "dd/plugin/constrain": {
            requires: ["base", "node"]
        }
    });
    a({
        "dd/plugin/proxy": {
            requires: ["node", "base", "dd"]
        }
    });
    a({
        "dd/plugin/scroll": {
            requires: ["dd", "base", "node"]
        }
    });
    a({
        "dom/basic": {
            alias: ["dom/base", e.isIELessThan(9) ? "dom/ie" : "", e.isClassListSupported() ? "" : "dom/class-list"]
        },
        dom: {
            alias: ["dom/basic", !e.isQuerySelectorSupported() ? "dom/selector" : ""]
        }
    });
    a({
        "dom/class-list": {
            requires: ["dom/base"]
        }
    });
    a({
        "dom/ie": {
            requires: ["dom/base"]
        }
    });
    a({
        "dom/selector": {
            requires: ["dom/basic"]
        }
    });
    a({
        editor: {
            requires: ["node", "html-parser", "component/control", "event"]
        }
    });
    a({
        event: {
            requires: ["event/dom", "event/custom"]
        }
    });
    a({
        "event/custom": {
            requires: ["event/base"]
        }
    });
    a({
        "event/dom": {
            alias: ["event/dom/base", e.isTouchEventSupported() || e.isMsPointerSupported() ? "event/dom/touch" : "", e.isDeviceMotionSupported() ? "event/dom/shake" :
                "", e.isHashChangeSupported() ? "" : "event/dom/hashchange", e.isIELessThan(9) ? "event/dom/ie" : "", g.ie ? "" : "event/dom/focusin"
            ]
        }
    });
    a({
        "event/dom/base": {
            requires: ["event/base", "dom"]
        }
    });
    a({
        "event/dom/focusin": {
            requires: ["event/dom/base"]
        }
    });
    a({
        "event/dom/hashchange": {
            requires: ["event/dom/base", "dom"]
        }
    });
    a({
        "event/dom/ie": {
            requires: ["event/dom/base", "dom"]
        }
    });
    a({
        "event/dom/shake": {
            requires: ["event/dom/base"]
        }
    });
    a({
        "event/dom/touch": {
            requires: ["event/dom/base", "dom"]
        }
    });
    a({
        "filter-menu": {
            requires: ["menu", "node",
                "component/extension/content-render"
            ]
        }
    });
    a({
        io: {
            requires: ["dom", "event/custom", "promise", "event"]
        }
    });
    a({
        kison: {
            requires: ["base"]
        }
    });
    a({
        menu: {
            requires: "node,component/container,component/extension/delegate-children,component/control,component/extension/content-render,component/extension/align,component/extension/shim".split(",")
        }
    });
    a({
        menubutton: {
            requires: ["node", "button", "component/extension/content-render", "menu"]
        }
    });
    a({
        mvc: {
            requires: ["base", "node", "io", "json"]
        }
    });
    a({
        node: {
            requires: ["dom", "event/dom",
                "anim"
            ]
        }
    });
    a({
        overlay: {
            requires: ["component/container", "component/extension/shim", "component/extension/align", "node", "component/extension/content-render"]
        }
    });
    a({
        resizable: {
            requires: ["node", "base", "dd"]
        }
    });
    a({
        "resizable/plugin/proxy": {
            requires: ["base", "node"]
        }
    });
    a({
        "scroll-view": {
            alias: [e.isTouchEventSupported() || e.isMsPointerSupported() ? "scroll-view/drag" : "scroll-view/base"]
        }
    });
    a({
        "scroll-view/base": {
            requires: ["node", "anim", "component/container", "component/extension/content-render"]
        }
    });
    a({
        "scroll-view/drag": {
            requires: ["scroll-view/base",
                "node", "anim"
            ]
        }
    });
    a({
        "scroll-view/plugin/pull-to-refresh": {
            requires: ["base"]
        }
    });
    a({
        "scroll-view/plugin/scrollbar": {
            requires: ["base", "node", "component/control"]
        }
    });
    a({
        separator: {
            requires: ["component/control"]
        }
    });
    a({
        "split-button": {
            requires: ["component/container", "button", "menubutton"]
        }
    });
    a({
        stylesheet: {
            requires: ["dom"]
        }
    });
    a({
        swf: {
            requires: ["dom", "json", "base"]
        }
    });
    a({
        tabs: {
            requires: ["component/container", "toolbar", "button"]
        }
    });
    a({
        toolbar: {
            requires: ["component/container", "component/extension/delegate-children",
                "node"
            ]
        }
    });
    a({
        tree: {
            requires: ["node", "component/container", "component/extension/content-render", "component/extension/delegate-children"]
        }
    });
    a({
        xtemplate: {
            requires: ["xtemplate/runtime", "xtemplate/compiler"]
        }
    });
    a({
        "xtemplate/compiler": {
            requires: ["xtemplate/runtime"]
        }
    });
    a({
        "xtemplate/nodejs": {
            requires: ["xtemplate"]
        }
    })
})(function (a) {
    KISSY.config("modules", a)
}, KISSY.Features, KISSY.UA);
(function (a) {
    a.add("empty", a.noop);
    a.add("ua", function () {
        return a.UA
    });
    a.add("uri", function () {
        return a.Uri
    });
    a.add("path", function () {
        return a.Path
    });
    var e = a.Env.host,
        g = (e.document || {}).documentMode,
        l = (a.UA.nodejs && "object" === typeof global ? global : e).JSON;
    g && 9 > g && (l = null);
    if (l) a.add("json", function () {
        return a.JSON = l
    }), a.parseJson = function (a) {
        return l.parse(a)
    };
    else {
        var k = /^[\],:{}\s]*$/,
            d = /(?:^|:|,)(?:\s*\[)+/g,
            f = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g,
            n = /"[^"\\\r\n]*"|true|false|null|-?(?:\d+\.|)\d+(?:[eE][+-]?\d+|)/g;
        a.parseJson = function (b) {
            return null === b ? b : "string" === typeof b && (b = a.trim(b)) && k.test(b.replace(f, "@").replace(n, "]").replace(d, "")) ? (new Function("return " + b))() : a.error("Invalid Json: " + b)
        }
    }
    a.UA.nodejs && (a.KISSY = a, module.exports = a)
})(KISSY);

/*
combined files : 

gallery/rsa/1.0/jsbn
gallery/rsa/1.0/prng4
gallery/rsa/1.0/rng
gallery/rsa/1.0/index

*/
/**
 * @fileoverview
 * @author 姊у繉 <wuji.xwt@alibaba-inc.com>
 * @module jsbn
 **/
KISSY.add('jsbn', function (S) {
    // Copyright (c) 2005  Tom Wu
    // All Rights Reserved.
    // See "LICENSE" for details.

    // Basic JavaScript BN library - subset useful for RSA encryption.

    // Bits per digit
    var dbits;

    // JavaScript engine analysis
    var canary = 0xdeadbeefcafe;
    var j_lm = ((canary & 0xffffff) == 0xefcafe);

    // (public) Constructor
    function BigInteger(a, b, c) {
        if (a != null)
            if ("number" == typeof a) this.fromNumber(a, b, c);
            else if (b == null && "string" != typeof a) this.fromString(a, 256);
        else this.fromString(a, b);
    }

    // return new, unset BigInteger
    function nbi() {
        return new BigInteger(null);
    }

    // am: Compute w_j += (x*this_i), propagate carries,
    // c is initial carry, returns final carry.
    // c < 3*dvalue, x < 2*dvalue, this_i < dvalue
    // We need to select the fastest one that works in this environment.

    // am1: use a single mult and divide to get the high bits,
    // max digit bits should be 26 because
    // max internal value = 2*dvalue^2-2*dvalue (< 2^53)
    function am1(i, x, w, j, c, n) {
            while (--n >= 0) {
                var v = x * this[i++] + w[j] + c;
                c = Math.floor(v / 0x4000000);
                w[j++] = v & 0x3ffffff;
            }
            return c;
        }
        // am2 avoids a big mult-and-extract completely.
        // Max digit bits should be <= 30 because we do bitwise ops
        // on values up to 2*hdvalue^2-hdvalue-1 (< 2^31)

    function am2(i, x, w, j, c, n) {
            var xl = x & 0x7fff,
                xh = x >> 15;
            while (--n >= 0) {
                var l = this[i] & 0x7fff;
                var h = this[i++] >> 15;
                var m = xh * l + h * xl;
                l = xl * l + ((m & 0x7fff) << 15) + w[j] + (c & 0x3fffffff);
                c = (l >>> 30) + (m >>> 15) + xh * h + (c >>> 30);
                w[j++] = l & 0x3fffffff;
            }
            return c;
        }
        // Alternately, set max digit bits to 28 since some
        // browsers slow down when dealing with 32-bit numbers.

    function am3(i, x, w, j, c, n) {
        var xl = x & 0x3fff,
            xh = x >> 14;
        while (--n >= 0) {
            var l = this[i] & 0x3fff;
            var h = this[i++] >> 14;
            var m = xh * l + h * xl;
            l = xl * l + ((m & 0x3fff) << 14) + w[j] + c;
            c = (l >> 28) + (m >> 14) + xh * h;
            w[j++] = l & 0xfffffff;
        }
        return c;
    }
    if (j_lm && (navigator.appName == "Microsoft Internet Explorer")) {
        BigInteger.prototype.am = am2;
        dbits = 30;
    } else if (j_lm && (navigator.appName != "Netscape")) {
        BigInteger.prototype.am = am1;
        dbits = 26;
    } else { // Mozilla/Netscape seems to prefer am3
        BigInteger.prototype.am = am3;
        dbits = 28;
    }

    BigInteger.prototype.DB = dbits;
    BigInteger.prototype.DM = ((1 << dbits) - 1);
    BigInteger.prototype.DV = (1 << dbits);

    var BI_FP = 52;
    BigInteger.prototype.FV = Math.pow(2, BI_FP);
    BigInteger.prototype.F1 = BI_FP - dbits;
    BigInteger.prototype.F2 = 2 * dbits - BI_FP;

    // Digit conversions
    var BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
    var BI_RC = new Array();
    var rr, vv;
    rr = "0".charCodeAt(0);
    for (vv = 0; vv <= 9; ++vv) BI_RC[rr++] = vv;
    rr = "a".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;
    rr = "A".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;

    function int2char(n) {
        return BI_RM.charAt(n);
    }

    function intAt(s, i) {
        var c = BI_RC[s.charCodeAt(i)];
        return (c == null) ? -1 : c;
    }

    // (protected) copy this to r
    function bnpCopyTo(r) {
        for (var i = this.t - 1; i >= 0; --i) r[i] = this[i];
        r.t = this.t;
        r.s = this.s;
    }

    // (protected) set from integer value x, -DV <= x < DV
    function bnpFromInt(x) {
        this.t = 1;
        this.s = (x < 0) ? -1 : 0;
        if (x > 0) this[0] = x;
        else if (x < -1) this[0] = x + this.DV;
        else this.t = 0;
    }

    // return bigint initialized to value
    function nbv(i) {
        var r = nbi();
        r.fromInt(i);
        return r;
    }

    // (protected) set from string and radix
    function bnpFromString(s, b) {
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 256) k = 8; // byte array
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else {
            this.fromRadix(s, b);
            return;
        }
        this.t = 0;
        this.s = 0;
        var i = s.length,
            mi = false,
            sh = 0;
        while (--i >= 0) {
            var x = (k == 8) ? s[i] & 0xff : intAt(s, i);
            if (x < 0) {
                if (s.charAt(i) == "-") mi = true;
                continue;
            }
            mi = false;
            if (sh == 0)
                this[this.t++] = x;
            else if (sh + k > this.DB) {
                this[this.t - 1] |= (x & ((1 << (this.DB - sh)) - 1)) << sh;
                this[this.t++] = (x >> (this.DB - sh));
            } else
                this[this.t - 1] |= x << sh;
            sh += k;
            if (sh >= this.DB) sh -= this.DB;
        }
        if (k == 8 && (s[0] & 0x80) != 0) {
            this.s = -1;
            if (sh > 0) this[this.t - 1] |= ((1 << (this.DB - sh)) - 1) << sh;
        }
        this.clamp();
        if (mi) BigInteger.ZERO.subTo(this, this);
    }

    // (protected) clamp off excess high words
    function bnpClamp() {
        var c = this.s & this.DM;
        while (this.t > 0 && this[this.t - 1] == c)--this.t;
    }

    // (public) return string representation in given radix
    function bnToString(b) {
        if (this.s < 0) return "-" + this.negate().toString(b);
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else return this.toRadix(b);
        var km = (1 << k) - 1,
            d, m = false,
            r = "",
            i = this.t;
        var p = this.DB - (i * this.DB) % k;
        if (i-- > 0) {
            if (p < this.DB && (d = this[i] >> p) > 0) {
                m = true;
                r = int2char(d);
            }
            while (i >= 0) {
                if (p < k) {
                    d = (this[i] & ((1 << p) - 1)) << (k - p);
                    d |= this[--i] >> (p += this.DB - k);
                } else {
                    d = (this[i] >> (p -= k)) & km;
                    if (p <= 0) {
                        p += this.DB;
                        --i;
                    }
                }
                if (d > 0) m = true;
                if (m) r += int2char(d);
            }
        }
        return m ? r : "0";
    }

    // (public) -this
    function bnNegate() {
        var r = nbi();
        BigInteger.ZERO.subTo(this, r);
        return r;
    }

    // (public) |this|
    function bnAbs() {
        return (this.s < 0) ? this.negate() : this;
    }

    // (public) return + if this > a, - if this < a, 0 if equal
    function bnCompareTo(a) {
        var r = this.s - a.s;
        if (r != 0) return r;
        var i = this.t;
        r = i - a.t;
        if (r != 0) return (this.s < 0) ? -r : r;
        while (--i >= 0)
            if ((r = this[i] - a[i]) != 0) return r;
        return 0;
    }

    // returns bit length of the integer x
    function nbits(x) {
        var r = 1,
            t;
        if ((t = x >>> 16) != 0) {
            x = t;
            r += 16;
        }
        if ((t = x >> 8) != 0) {
            x = t;
            r += 8;
        }
        if ((t = x >> 4) != 0) {
            x = t;
            r += 4;
        }
        if ((t = x >> 2) != 0) {
            x = t;
            r += 2;
        }
        if ((t = x >> 1) != 0) {
            x = t;
            r += 1;
        }
        return r;
    }

    // (public) return the number of bits in "this"
    function bnBitLength() {
        if (this.t <= 0) return 0;
        return this.DB * (this.t - 1) + nbits(this[this.t - 1] ^ (this.s & this.DM));
    }

    // (protected) r = this << n*DB
    function bnpDLShiftTo(n, r) {
        var i;
        for (i = this.t - 1; i >= 0; --i) r[i + n] = this[i];
        for (i = n - 1; i >= 0; --i) r[i] = 0;
        r.t = this.t + n;
        r.s = this.s;
    }

    // (protected) r = this >> n*DB
    function bnpDRShiftTo(n, r) {
        for (var i = n; i < this.t; ++i) r[i - n] = this[i];
        r.t = Math.max(this.t - n, 0);
        r.s = this.s;
    }

    // (protected) r = this << n
    function bnpLShiftTo(n, r) {
        var bs = n % this.DB;
        var cbs = this.DB - bs;
        var bm = (1 << cbs) - 1;
        var ds = Math.floor(n / this.DB),
            c = (this.s << bs) & this.DM,
            i;
        for (i = this.t - 1; i >= 0; --i) {
            r[i + ds + 1] = (this[i] >> cbs) | c;
            c = (this[i] & bm) << bs;
        }
        for (i = ds - 1; i >= 0; --i) r[i] = 0;
        r[ds] = c;
        r.t = this.t + ds + 1;
        r.s = this.s;
        r.clamp();
    }

    // (protected) r = this >> n
    function bnpRShiftTo(n, r) {
        r.s = this.s;
        var ds = Math.floor(n / this.DB);
        if (ds >= this.t) {
            r.t = 0;
            return;
        }
        var bs = n % this.DB;
        var cbs = this.DB - bs;
        var bm = (1 << bs) - 1;
        r[0] = this[ds] >> bs;
        for (var i = ds + 1; i < this.t; ++i) {
            r[i - ds - 1] |= (this[i] & bm) << cbs;
            r[i - ds] = this[i] >> bs;
        }
        if (bs > 0) r[this.t - ds - 1] |= (this.s & bm) << cbs;
        r.t = this.t - ds;
        r.clamp();
    }

    // (protected) r = this - a
    function bnpSubTo(a, r) {
        var i = 0,
            c = 0,
            m = Math.min(a.t, this.t);
        while (i < m) {
            c += this[i] - a[i];
            r[i++] = c & this.DM;
            c >>= this.DB;
        }
        if (a.t < this.t) {
            c -= a.s;
            while (i < this.t) {
                c += this[i];
                r[i++] = c & this.DM;
                c >>= this.DB;
            }
            c += this.s;
        } else {
            c += this.s;
            while (i < a.t) {
                c -= a[i];
                r[i++] = c & this.DM;
                c >>= this.DB;
            }
            c -= a.s;
        }
        r.s = (c < 0) ? -1 : 0;
        if (c < -1) r[i++] = this.DV + c;
        else if (c > 0) r[i++] = c;
        r.t = i;
        r.clamp();
    }

    // (protected) r = this * a, r != this,a (HAC 14.12)
    // "this" should be the larger one if appropriate.
    function bnpMultiplyTo(a, r) {
        var x = this.abs(),
            y = a.abs();
        var i = x.t;
        r.t = i + y.t;
        while (--i >= 0) r[i] = 0;
        for (i = 0; i < y.t; ++i) r[i + x.t] = x.am(0, y[i], r, i, 0, x.t);
        r.s = 0;
        r.clamp();
        if (this.s != a.s) BigInteger.ZERO.subTo(r, r);
    }

    // (protected) r = this^2, r != this (HAC 14.16)
    function bnpSquareTo(r) {
        var x = this.abs();
        var i = r.t = 2 * x.t;
        while (--i >= 0) r[i] = 0;
        for (i = 0; i < x.t - 1; ++i) {
            var c = x.am(i, x[i], r, 2 * i, 0, 1);
            if ((r[i + x.t] += x.am(i + 1, 2 * x[i], r, 2 * i + 1, c, x.t - i - 1)) >= x.DV) {
                r[i + x.t] -= x.DV;
                r[i + x.t + 1] = 1;
            }
        }
        if (r.t > 0) r[r.t - 1] += x.am(i, x[i], r, 2 * i, 0, 1);
        r.s = 0;
        r.clamp();
    }

    // (protected) divide this by m, quotient and remainder to q, r (HAC 14.20)
    // r != q, this != m.  q or r may be null.
    function bnpDivRemTo(m, q, r) {
        var pm = m.abs();
        if (pm.t <= 0) return;
        var pt = this.abs();
        if (pt.t < pm.t) {
            if (q != null) q.fromInt(0);
            if (r != null) this.copyTo(r);
            return;
        }
        if (r == null) r = nbi();
        var y = nbi(),
            ts = this.s,
            ms = m.s;
        var nsh = this.DB - nbits(pm[pm.t - 1]); // normalize modulus
        if (nsh > 0) {
            pm.lShiftTo(nsh, y);
            pt.lShiftTo(nsh, r);
        } else {
            pm.copyTo(y);
            pt.copyTo(r);
        }
        var ys = y.t;
        var y0 = y[ys - 1];
        if (y0 == 0) return;
        var yt = y0 * (1 << this.F1) + ((ys > 1) ? y[ys - 2] >> this.F2 : 0);
        var d1 = this.FV / yt,
            d2 = (1 << this.F1) / yt,
            e = 1 << this.F2;
        var i = r.t,
            j = i - ys,
            t = (q == null) ? nbi() : q;
        y.dlShiftTo(j, t);
        if (r.compareTo(t) >= 0) {
            r[r.t++] = 1;
            r.subTo(t, r);
        }
        BigInteger.ONE.dlShiftTo(ys, t);
        t.subTo(y, y); // "negative" y so we can replace sub with am later
        while (y.t < ys) y[y.t++] = 0;
        while (--j >= 0) {
            // Estimate quotient digit
            var qd = (r[--i] == y0) ? this.DM : Math.floor(r[i] * d1 + (r[i - 1] + e) * d2);
            if ((r[i] += y.am(0, qd, r, j, 0, ys)) < qd) { // Try it out
                y.dlShiftTo(j, t);
                r.subTo(t, r);
                while (r[i] < --qd) r.subTo(t, r);
            }
        }
        if (q != null) {
            r.drShiftTo(ys, q);
            if (ts != ms) BigInteger.ZERO.subTo(q, q);
        }
        r.t = ys;
        r.clamp();
        if (nsh > 0) r.rShiftTo(nsh, r); // Denormalize remainder
        if (ts < 0) BigInteger.ZERO.subTo(r, r);
    }

    // (public) this mod a
    function bnMod(a) {
        var r = nbi();
        this.abs().divRemTo(a, null, r);
        if (this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r, r);
        return r;
    }

    // Modular reduction using "classic" algorithm
    function Classic(m) {
        this.m = m;
    }

    function cConvert(x) {
        if (x.s < 0 || x.compareTo(this.m) >= 0) return x.mod(this.m);
        else return x;
    }

    function cRevert(x) {
        return x;
    }

    function cReduce(x) {
        x.divRemTo(this.m, null, x);
    }

    function cMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r);
    }

    function cSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r);
    }

    Classic.prototype.convert = cConvert;
    Classic.prototype.revert = cRevert;
    Classic.prototype.reduce = cReduce;
    Classic.prototype.mulTo = cMulTo;
    Classic.prototype.sqrTo = cSqrTo;

    // (protected) return "-1/this % 2^DB"; useful for Mont. reduction
    // justification:
    //         xy == 1 (mod m)
    //         xy =  1+km
    //   xy(2-xy) = (1+km)(1-km)
    // x[y(2-xy)] = 1-k^2m^2
    // x[y(2-xy)] == 1 (mod m^2)
    // if y is 1/x mod m, then y(2-xy) is 1/x mod m^2
    // should reduce x and y(2-xy) by m^2 at each step to keep size bounded.
    // JS multiply "overflows" differently from C/C++, so care is needed here.
    function bnpInvDigit() {
        if (this.t < 1) return 0;
        var x = this[0];
        if ((x & 1) == 0) return 0;
        var y = x & 3; // y == 1/x mod 2^2
        y = (y * (2 - (x & 0xf) * y)) & 0xf; // y == 1/x mod 2^4
        y = (y * (2 - (x & 0xff) * y)) & 0xff; // y == 1/x mod 2^8
        y = (y * (2 - (((x & 0xffff) * y) & 0xffff))) & 0xffff; // y == 1/x mod 2^16
        // last step - calculate inverse mod DV directly;
        // assumes 16 < DB <= 32 and assumes ability to handle 48-bit ints
        y = (y * (2 - x * y % this.DV)) % this.DV; // y == 1/x mod 2^dbits
        // we really want the negative inverse, and -DV < y < DV
        return (y > 0) ? this.DV - y : -y;
    }

    // Montgomery reduction
    function Montgomery(m) {
        this.m = m;
        this.mp = m.invDigit();
        this.mpl = this.mp & 0x7fff;
        this.mph = this.mp >> 15;
        this.um = (1 << (m.DB - 15)) - 1;
        this.mt2 = 2 * m.t;
    }

    // xR mod m
    function montConvert(x) {
        var r = nbi();
        x.abs().dlShiftTo(this.m.t, r);
        r.divRemTo(this.m, null, r);
        if (x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r, r);
        return r;
    }

    // x/R mod m
    function montRevert(x) {
        var r = nbi();
        x.copyTo(r);
        this.reduce(r);
        return r;
    }

    // x = x/R mod m (HAC 14.32)
    function montReduce(x) {
        while (x.t <= this.mt2) // pad x so am has enough room later
            x[x.t++] = 0;
        for (var i = 0; i < this.m.t; ++i) {
            // faster way of calculating u0 = x[i]*mp mod DV
            var j = x[i] & 0x7fff;
            var u0 = (j * this.mpl + (((j * this.mph + (x[i] >> 15) * this.mpl) & this.um) << 15)) & x.DM;
            // use am to combine the multiply-shift-add into one call
            j = i + this.m.t;
            x[j] += this.m.am(0, u0, x, i, 0, this.m.t);
            // propagate carry
            while (x[j] >= x.DV) {
                x[j] -= x.DV;
                x[++j]++;
            }
        }
        x.clamp();
        x.drShiftTo(this.m.t, x);
        if (x.compareTo(this.m) >= 0) x.subTo(this.m, x);
    }

    // r = "x^2/R mod m"; x != r
    function montSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r);
    }

    // r = "xy/R mod m"; x,y != r
    function montMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r);
    }

    Montgomery.prototype.convert = montConvert;
    Montgomery.prototype.revert = montRevert;
    Montgomery.prototype.reduce = montReduce;
    Montgomery.prototype.mulTo = montMulTo;
    Montgomery.prototype.sqrTo = montSqrTo;

    // (protected) true iff this is even
    function bnpIsEven() {
        return ((this.t > 0) ? (this[0] & 1) : this.s) == 0;
    }

    // (protected) this^e, e < 2^32, doing sqr and mul with "r" (HAC 14.79)
    function bnpExp(e, z) {
        if (e > 0xffffffff || e < 1) return BigInteger.ONE;
        var r = nbi(),
            r2 = nbi(),
            g = z.convert(this),
            i = nbits(e) - 1;
        g.copyTo(r);
        while (--i >= 0) {
            z.sqrTo(r, r2);
            if ((e & (1 << i)) > 0) z.mulTo(r2, g, r);
            else {
                var t = r;
                r = r2;
                r2 = t;
            }
        }
        return z.revert(r);
    }

    // (public) this^e % m, 0 <= e < 2^32
    function bnModPowInt(e, m) {
        var z;
        if (e < 256 || m.isEven()) z = new Classic(m);
        else z = new Montgomery(m);
        return this.exp(e, z);
    }

    // protected
    BigInteger.prototype.copyTo = bnpCopyTo;
    BigInteger.prototype.fromInt = bnpFromInt;
    BigInteger.prototype.fromString = bnpFromString;
    BigInteger.prototype.clamp = bnpClamp;
    BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
    BigInteger.prototype.drShiftTo = bnpDRShiftTo;
    BigInteger.prototype.lShiftTo = bnpLShiftTo;
    BigInteger.prototype.rShiftTo = bnpRShiftTo;
    BigInteger.prototype.subTo = bnpSubTo;
    BigInteger.prototype.multiplyTo = bnpMultiplyTo;
    BigInteger.prototype.squareTo = bnpSquareTo;
    BigInteger.prototype.divRemTo = bnpDivRemTo;
    BigInteger.prototype.invDigit = bnpInvDigit;
    BigInteger.prototype.isEven = bnpIsEven;
    BigInteger.prototype.exp = bnpExp;

    // public
    BigInteger.prototype.toString = bnToString;
    BigInteger.prototype.negate = bnNegate;
    BigInteger.prototype.abs = bnAbs;
    BigInteger.prototype.compareTo = bnCompareTo;
    BigInteger.prototype.bitLength = bnBitLength;
    BigInteger.prototype.mod = bnMod;
    BigInteger.prototype.modPowInt = bnModPowInt;

    // "constants"
    BigInteger.ZERO = nbv(0);
    BigInteger.ONE = nbv(1);

    //=============================================jsbn2.js

    // Copyright (c) 2005-2009  Tom Wu
    // All Rights Reserved.
    // See "LICENSE" for details.

    // Extended JavaScript BN functions, required for RSA private ops.

    // Version 1.1: new BigInteger("0", 10) returns "proper" zero
    // Version 1.2: square() API, isProbablePrime fix

    // (public)
    function bnClone() {
        var r = nbi();
        this.copyTo(r);
        return r;
    }

    // (public) return value as integer
    function bnIntValue() {
        if (this.s < 0) {
            if (this.t == 1) return this[0] - this.DV;
            else if (this.t == 0) return -1;
        } else if (this.t == 1) return this[0];
        else if (this.t == 0) return 0;
        // assumes 16 < DB < 32
        return ((this[1] & ((1 << (32 - this.DB)) - 1)) << this.DB) | this[0];
    }

    // (public) return value as byte
    function bnByteValue() {
        return (this.t == 0) ? this.s : (this[0] << 24) >> 24;
    }

    // (public) return value as short (assumes DB>=16)
    function bnShortValue() {
        return (this.t == 0) ? this.s : (this[0] << 16) >> 16;
    }

    // (protected) return x s.t. r^x < DV
    function bnpChunkSize(r) {
        return Math.floor(Math.LN2 * this.DB / Math.log(r));
    }

    // (public) 0 if this == 0, 1 if this > 0
    function bnSigNum() {
        if (this.s < 0) return -1;
        else if (this.t <= 0 || (this.t == 1 && this[0] <= 0)) return 0;
        else return 1;
    }

    // (protected) convert to radix string
    function bnpToRadix(b) {
        if (b == null) b = 10;
        if (this.signum() == 0 || b < 2 || b > 36) return "0";
        var cs = this.chunkSize(b);
        var a = Math.pow(b, cs);
        var d = nbv(a),
            y = nbi(),
            z = nbi(),
            r = "";
        this.divRemTo(d, y, z);
        while (y.signum() > 0) {
            r = (a + z.intValue()).toString(b).substr(1) + r;
            y.divRemTo(d, y, z);
        }
        return z.intValue().toString(b) + r;
    }

    // (protected) convert from radix string
    function bnpFromRadix(s, b) {
        this.fromInt(0);
        if (b == null) b = 10;
        var cs = this.chunkSize(b);
        var d = Math.pow(b, cs),
            mi = false,
            j = 0,
            w = 0;
        for (var i = 0; i < s.length; ++i) {
            var x = intAt(s, i);
            if (x < 0) {
                if (s.charAt(i) == "-" && this.signum() == 0) mi = true;
                continue;
            }
            w = b * w + x;
            if (++j >= cs) {
                this.dMultiply(d);
                this.dAddOffset(w, 0);
                j = 0;
                w = 0;
            }
        }
        if (j > 0) {
            this.dMultiply(Math.pow(b, j));
            this.dAddOffset(w, 0);
        }
        if (mi) BigInteger.ZERO.subTo(this, this);
    }

    // (protected) alternate constructor
    function bnpFromNumber(a, b, c) {
        if ("number" == typeof b) {
            // new BigInteger(int,int,RNG)
            if (a < 2) this.fromInt(1);
            else {
                this.fromNumber(a, c);
                if (!this.testBit(a - 1)) // force MSB set
                    this.bitwiseTo(BigInteger.ONE.shiftLeft(a - 1), op_or, this);
                if (this.isEven()) this.dAddOffset(1, 0); // force odd
                while (!this.isProbablePrime(b)) {
                    this.dAddOffset(2, 0);
                    if (this.bitLength() > a) this.subTo(BigInteger.ONE.shiftLeft(a - 1), this);
                }
            }
        } else {
            // new BigInteger(int,RNG)
            var x = new Array(),
                t = a & 7;
            x.length = (a >> 3) + 1;
            b.nextBytes(x);
            if (t > 0) x[0] &= ((1 << t) - 1);
            else x[0] = 0;
            this.fromString(x, 256);
        }
    }

    // (public) convert to bigendian byte array
    function bnToByteArray() {
        var i = this.t,
            r = new Array();
        r[0] = this.s;
        var p = this.DB - (i * this.DB) % 8,
            d, k = 0;
        if (i-- > 0) {
            if (p < this.DB && (d = this[i] >> p) != (this.s & this.DM) >> p)
                r[k++] = d | (this.s << (this.DB - p));
            while (i >= 0) {
                if (p < 8) {
                    d = (this[i] & ((1 << p) - 1)) << (8 - p);
                    d |= this[--i] >> (p += this.DB - 8);
                } else {
                    d = (this[i] >> (p -= 8)) & 0xff;
                    if (p <= 0) {
                        p += this.DB;
                        --i;
                    }
                }
                if ((d & 0x80) != 0) d |= -256;
                if (k == 0 && (this.s & 0x80) != (d & 0x80))++k;
                if (k > 0 || d != this.s) r[k++] = d;
            }
        }
        return r;
    }

    function bnEquals(a) {
        return (this.compareTo(a) == 0);
    }

    function bnMin(a) {
        return (this.compareTo(a) < 0) ? this : a;
    }

    function bnMax(a) {
        return (this.compareTo(a) > 0) ? this : a;
    }

    // (protected) r = this op a (bitwise)
    function bnpBitwiseTo(a, op, r) {
        var i, f, m = Math.min(a.t, this.t);
        for (i = 0; i < m; ++i) r[i] = op(this[i], a[i]);
        if (a.t < this.t) {
            f = a.s & this.DM;
            for (i = m; i < this.t; ++i) r[i] = op(this[i], f);
            r.t = this.t;
        } else {
            f = this.s & this.DM;
            for (i = m; i < a.t; ++i) r[i] = op(f, a[i]);
            r.t = a.t;
        }
        r.s = op(this.s, a.s);
        r.clamp();
    }

    // (public) this & a
    function op_and(x, y) {
        return x & y;
    }

    function bnAnd(a) {
        var r = nbi();
        this.bitwiseTo(a, op_and, r);
        return r;
    }

    // (public) this | a
    function op_or(x, y) {
        return x | y;
    }

    function bnOr(a) {
        var r = nbi();
        this.bitwiseTo(a, op_or, r);
        return r;
    }

    // (public) this ^ a
    function op_xor(x, y) {
        return x ^ y;
    }

    function bnXor(a) {
        var r = nbi();
        this.bitwiseTo(a, op_xor, r);
        return r;
    }

    // (public) this & ~a
    function op_andnot(x, y) {
        return x & ~y;
    }

    function bnAndNot(a) {
        var r = nbi();
        this.bitwiseTo(a, op_andnot, r);
        return r;
    }

    // (public) ~this
    function bnNot() {
        var r = nbi();
        for (var i = 0; i < this.t; ++i) r[i] = this.DM & ~this[i];
        r.t = this.t;
        r.s = ~this.s;
        return r;
    }

    // (public) this << n
    function bnShiftLeft(n) {
        var r = nbi();
        if (n < 0) this.rShiftTo(-n, r);
        else this.lShiftTo(n, r);
        return r;
    }

    // (public) this >> n
    function bnShiftRight(n) {
        var r = nbi();
        if (n < 0) this.lShiftTo(-n, r);
        else this.rShiftTo(n, r);
        return r;
    }

    // return index of lowest 1-bit in x, x < 2^31
    function lbit(x) {
        if (x == 0) return -1;
        var r = 0;
        if ((x & 0xffff) == 0) {
            x >>= 16;
            r += 16;
        }
        if ((x & 0xff) == 0) {
            x >>= 8;
            r += 8;
        }
        if ((x & 0xf) == 0) {
            x >>= 4;
            r += 4;
        }
        if ((x & 3) == 0) {
            x >>= 2;
            r += 2;
        }
        if ((x & 1) == 0)++r;
        return r;
    }

    // (public) returns index of lowest 1-bit (or -1 if none)
    function bnGetLowestSetBit() {
        for (var i = 0; i < this.t; ++i)
            if (this[i] != 0) return i * this.DB + lbit(this[i]);
        if (this.s < 0) return this.t * this.DB;
        return -1;
    }

    // return number of 1 bits in x
    function cbit(x) {
        var r = 0;
        while (x != 0) {
            x &= x - 1;
            ++r;
        }
        return r;
    }

    // (public) return number of set bits
    function bnBitCount() {
        var r = 0,
            x = this.s & this.DM;
        for (var i = 0; i < this.t; ++i) r += cbit(this[i] ^ x);
        return r;
    }

    // (public) true iff nth bit is set
    function bnTestBit(n) {
        var j = Math.floor(n / this.DB);
        if (j >= this.t) return (this.s != 0);
        return ((this[j] & (1 << (n % this.DB))) != 0);
    }

    // (protected) this op (1<<n)
    function bnpChangeBit(n, op) {
        var r = BigInteger.ONE.shiftLeft(n);
        this.bitwiseTo(r, op, r);
        return r;
    }

    // (public) this | (1<<n)
    function bnSetBit(n) {
        return this.changeBit(n, op_or);
    }

    // (public) this & ~(1<<n)
    function bnClearBit(n) {
        return this.changeBit(n, op_andnot);
    }

    // (public) this ^ (1<<n)
    function bnFlipBit(n) {
        return this.changeBit(n, op_xor);
    }

    // (protected) r = this + a
    function bnpAddTo(a, r) {
        var i = 0,
            c = 0,
            m = Math.min(a.t, this.t);
        while (i < m) {
            c += this[i] + a[i];
            r[i++] = c & this.DM;
            c >>= this.DB;
        }
        if (a.t < this.t) {
            c += a.s;
            while (i < this.t) {
                c += this[i];
                r[i++] = c & this.DM;
                c >>= this.DB;
            }
            c += this.s;
        } else {
            c += this.s;
            while (i < a.t) {
                c += a[i];
                r[i++] = c & this.DM;
                c >>= this.DB;
            }
            c += a.s;
        }
        r.s = (c < 0) ? -1 : 0;
        if (c > 0) r[i++] = c;
        else if (c < -1) r[i++] = this.DV + c;
        r.t = i;
        r.clamp();
    }

    // (public) this + a
    function bnAdd(a) {
        var r = nbi();
        this.addTo(a, r);
        return r;
    }

    // (public) this - a
    function bnSubtract(a) {
        var r = nbi();
        this.subTo(a, r);
        return r;
    }

    // (public) this * a
    function bnMultiply(a) {
        var r = nbi();
        this.multiplyTo(a, r);
        return r;
    }

    // (public) this^2
    function bnSquare() {
        var r = nbi();
        this.squareTo(r);
        return r;
    }

    // (public) this / a
    function bnDivide(a) {
        var r = nbi();
        this.divRemTo(a, r, null);
        return r;
    }

    // (public) this % a
    function bnRemainder(a) {
        var r = nbi();
        this.divRemTo(a, null, r);
        return r;
    }

    // (public) [this/a,this%a]
    function bnDivideAndRemainder(a) {
        var q = nbi(),
            r = nbi();
        this.divRemTo(a, q, r);
        return new Array(q, r);
    }

    // (protected) this *= n, this >= 0, 1 < n < DV
    function bnpDMultiply(n) {
        this[this.t] = this.am(0, n - 1, this, 0, 0, this.t);
        ++this.t;
        this.clamp();
    }

    // (protected) this += n << w words, this >= 0
    function bnpDAddOffset(n, w) {
        if (n == 0) return;
        while (this.t <= w) this[this.t++] = 0;
        this[w] += n;
        while (this[w] >= this.DV) {
            this[w] -= this.DV;
            if (++w >= this.t) this[this.t++] = 0;
            ++this[w];
        }
    }

    // A "null" reducer
    function NullExp() {}

    function nNop(x) {
        return x;
    }

    function nMulTo(x, y, r) {
        x.multiplyTo(y, r);
    }

    function nSqrTo(x, r) {
        x.squareTo(r);
    }

    NullExp.prototype.convert = nNop;
    NullExp.prototype.revert = nNop;
    NullExp.prototype.mulTo = nMulTo;
    NullExp.prototype.sqrTo = nSqrTo;

    // (public) this^e
    function bnPow(e) {
        return this.exp(e, new NullExp());
    }

    // (protected) r = lower n words of "this * a", a.t <= n
    // "this" should be the larger one if appropriate.
    function bnpMultiplyLowerTo(a, n, r) {
        var i = Math.min(this.t + a.t, n);
        r.s = 0; // assumes a,this >= 0
        r.t = i;
        while (i > 0) r[--i] = 0;
        var j;
        for (j = r.t - this.t; i < j; ++i) r[i + this.t] = this.am(0, a[i], r, i, 0, this.t);
        for (j = Math.min(a.t, n); i < j; ++i) this.am(0, a[i], r, i, 0, n - i);
        r.clamp();
    }

    // (protected) r = "this * a" without lower n words, n > 0
    // "this" should be the larger one if appropriate.
    function bnpMultiplyUpperTo(a, n, r) {
        --n;
        var i = r.t = this.t + a.t - n;
        r.s = 0; // assumes a,this >= 0
        while (--i >= 0) r[i] = 0;
        for (i = Math.max(n - this.t, 0); i < a.t; ++i)
            r[this.t + i - n] = this.am(n - i, a[i], r, 0, 0, this.t + i - n);
        r.clamp();
        r.drShiftTo(1, r);
    }

    // Barrett modular reduction
    function Barrett(m) {
        // setup Barrett
        this.r2 = nbi();
        this.q3 = nbi();
        BigInteger.ONE.dlShiftTo(2 * m.t, this.r2);
        this.mu = this.r2.divide(m);
        this.m = m;
    }

    function barrettConvert(x) {
        if (x.s < 0 || x.t > 2 * this.m.t) return x.mod(this.m);
        else if (x.compareTo(this.m) < 0) return x;
        else {
            var r = nbi();
            x.copyTo(r);
            this.reduce(r);
            return r;
        }
    }

    function barrettRevert(x) {
        return x;
    }

    // x = x mod m (HAC 14.42)
    function barrettReduce(x) {
        x.drShiftTo(this.m.t - 1, this.r2);
        if (x.t > this.m.t + 1) {
            x.t = this.m.t + 1;
            x.clamp();
        }
        this.mu.multiplyUpperTo(this.r2, this.m.t + 1, this.q3);
        this.m.multiplyLowerTo(this.q3, this.m.t + 1, this.r2);
        while (x.compareTo(this.r2) < 0) x.dAddOffset(1, this.m.t + 1);
        x.subTo(this.r2, x);
        while (x.compareTo(this.m) >= 0) x.subTo(this.m, x);
    }

    // r = x^2 mod m; x != r
    function barrettSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r);
    }

    // r = x*y mod m; x,y != r
    function barrettMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r);
    }

    Barrett.prototype.convert = barrettConvert;
    Barrett.prototype.revert = barrettRevert;
    Barrett.prototype.reduce = barrettReduce;
    Barrett.prototype.mulTo = barrettMulTo;
    Barrett.prototype.sqrTo = barrettSqrTo;

    // (public) this^e % m (HAC 14.85)
    function bnModPow(e, m) {
        var i = e.bitLength(),
            k, r = nbv(1),
            z;
        if (i <= 0) return r;
        else if (i < 18) k = 1;
        else if (i < 48) k = 3;
        else if (i < 144) k = 4;
        else if (i < 768) k = 5;
        else k = 6;
        if (i < 8)
            z = new Classic(m);
        else if (m.isEven())
            z = new Barrett(m);
        else
            z = new Montgomery(m);

        // precomputation
        var g = new Array(),
            n = 3,
            k1 = k - 1,
            km = (1 << k) - 1;
        g[1] = z.convert(this);
        if (k > 1) {
            var g2 = nbi();
            z.sqrTo(g[1], g2);
            while (n <= km) {
                g[n] = nbi();
                z.mulTo(g2, g[n - 2], g[n]);
                n += 2;
            }
        }

        var j = e.t - 1,
            w, is1 = true,
            r2 = nbi(),
            t;
        i = nbits(e[j]) - 1;
        while (j >= 0) {
            if (i >= k1) w = (e[j] >> (i - k1)) & km;
            else {
                w = (e[j] & ((1 << (i + 1)) - 1)) << (k1 - i);
                if (j > 0) w |= e[j - 1] >> (this.DB + i - k1);
            }

            n = k;
            while ((w & 1) == 0) {
                w >>= 1;
                --n;
            }
            if ((i -= n) < 0) {
                i += this.DB;
                --j;
            }
            if (is1) { // ret == 1, don't bother squaring or multiplying it
                g[w].copyTo(r);
                is1 = false;
            } else {
                while (n > 1) {
                    z.sqrTo(r, r2);
                    z.sqrTo(r2, r);
                    n -= 2;
                }
                if (n > 0) z.sqrTo(r, r2);
                else {
                    t = r;
                    r = r2;
                    r2 = t;
                }
                z.mulTo(r2, g[w], r);
            }

            while (j >= 0 && (e[j] & (1 << i)) == 0) {
                z.sqrTo(r, r2);
                t = r;
                r = r2;
                r2 = t;
                if (--i < 0) {
                    i = this.DB - 1;
                    --j;
                }
            }
        }
        return z.revert(r);
    }

    // (public) gcd(this,a) (HAC 14.54)
    function bnGCD(a) {
        var x = (this.s < 0) ? this.negate() : this.clone();
        var y = (a.s < 0) ? a.negate() : a.clone();
        if (x.compareTo(y) < 0) {
            var t = x;
            x = y;
            y = t;
        }
        var i = x.getLowestSetBit(),
            g = y.getLowestSetBit();
        if (g < 0) return x;
        if (i < g) g = i;
        if (g > 0) {
            x.rShiftTo(g, x);
            y.rShiftTo(g, y);
        }
        while (x.signum() > 0) {
            if ((i = x.getLowestSetBit()) > 0) x.rShiftTo(i, x);
            if ((i = y.getLowestSetBit()) > 0) y.rShiftTo(i, y);
            if (x.compareTo(y) >= 0) {
                x.subTo(y, x);
                x.rShiftTo(1, x);
            } else {
                y.subTo(x, y);
                y.rShiftTo(1, y);
            }
        }
        if (g > 0) y.lShiftTo(g, y);
        return y;
    }

    // (protected) this % n, n < 2^26
    function bnpModInt(n) {
        if (n <= 0) return 0;
        var d = this.DV % n,
            r = (this.s < 0) ? n - 1 : 0;
        if (this.t > 0)
            if (d == 0) r = this[0] % n;
            else
                for (var i = this.t - 1; i >= 0; --i) r = (d * r + this[i]) % n;
        return r;
    }

    // (public) 1/this % m (HAC 14.61)
    function bnModInverse(m) {
        var ac = m.isEven();
        if ((this.isEven() && ac) || m.signum() == 0) return BigInteger.ZERO;
        var u = m.clone(),
            v = this.clone();
        var a = nbv(1),
            b = nbv(0),
            c = nbv(0),
            d = nbv(1);
        while (u.signum() != 0) {
            while (u.isEven()) {
                u.rShiftTo(1, u);
                if (ac) {
                    if (!a.isEven() || !b.isEven()) {
                        a.addTo(this, a);
                        b.subTo(m, b);
                    }
                    a.rShiftTo(1, a);
                } else if (!b.isEven()) b.subTo(m, b);
                b.rShiftTo(1, b);
            }
            while (v.isEven()) {
                v.rShiftTo(1, v);
                if (ac) {
                    if (!c.isEven() || !d.isEven()) {
                        c.addTo(this, c);
                        d.subTo(m, d);
                    }
                    c.rShiftTo(1, c);
                } else if (!d.isEven()) d.subTo(m, d);
                d.rShiftTo(1, d);
            }
            if (u.compareTo(v) >= 0) {
                u.subTo(v, u);
                if (ac) a.subTo(c, a);
                b.subTo(d, b);
            } else {
                v.subTo(u, v);
                if (ac) c.subTo(a, c);
                d.subTo(b, d);
            }
        }
        if (v.compareTo(BigInteger.ONE) != 0) return BigInteger.ZERO;
        if (d.compareTo(m) >= 0) return d.subtract(m);
        if (d.signum() < 0) d.addTo(m, d);
        else return d;
        if (d.signum() < 0) return d.add(m);
        else return d;
    }

    var lowprimes = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997];
    var lplim = (1 << 26) / lowprimes[lowprimes.length - 1];

    // (public) test primality with certainty >= 1-.5^t
    function bnIsProbablePrime(t) {
        var i, x = this.abs();
        if (x.t == 1 && x[0] <= lowprimes[lowprimes.length - 1]) {
            for (i = 0; i < lowprimes.length; ++i)
                if (x[0] == lowprimes[i]) return true;
            return false;
        }
        if (x.isEven()) return false;
        i = 1;
        while (i < lowprimes.length) {
            var m = lowprimes[i],
                j = i + 1;
            while (j < lowprimes.length && m < lplim) m *= lowprimes[j++];
            m = x.modInt(m);
            while (i < j)
                if (m % lowprimes[i++] == 0) return false;
        }
        return x.millerRabin(t);
    }

    // (protected) true if probably prime (HAC 4.24, Miller-Rabin)
    function bnpMillerRabin(t) {
        var n1 = this.subtract(BigInteger.ONE);
        var k = n1.getLowestSetBit();
        if (k <= 0) return false;
        var r = n1.shiftRight(k);
        t = (t + 1) >> 1;
        if (t > lowprimes.length) t = lowprimes.length;
        var a = nbi();
        for (var i = 0; i < t; ++i) {
            //Pick bases at random, instead of starting at 2
            a.fromInt(lowprimes[Math.floor(Math.random() * lowprimes.length)]);
            var y = a.modPow(r, this);
            if (y.compareTo(BigInteger.ONE) != 0 && y.compareTo(n1) != 0) {
                var j = 1;
                while (j++ < k && y.compareTo(n1) != 0) {
                    y = y.modPowInt(2, this);
                    if (y.compareTo(BigInteger.ONE) == 0) return false;
                }
                if (y.compareTo(n1) != 0) return false;
            }
        }
        return true;
    }

    // protected
    BigInteger.prototype.chunkSize = bnpChunkSize;
    BigInteger.prototype.toRadix = bnpToRadix;
    BigInteger.prototype.fromRadix = bnpFromRadix;
    BigInteger.prototype.fromNumber = bnpFromNumber;
    BigInteger.prototype.bitwiseTo = bnpBitwiseTo;
    BigInteger.prototype.changeBit = bnpChangeBit;
    BigInteger.prototype.addTo = bnpAddTo;
    BigInteger.prototype.dMultiply = bnpDMultiply;
    BigInteger.prototype.dAddOffset = bnpDAddOffset;
    BigInteger.prototype.multiplyLowerTo = bnpMultiplyLowerTo;
    BigInteger.prototype.multiplyUpperTo = bnpMultiplyUpperTo;
    BigInteger.prototype.modInt = bnpModInt;
    BigInteger.prototype.millerRabin = bnpMillerRabin;

    // public
    BigInteger.prototype.clone = bnClone;
    BigInteger.prototype.intValue = bnIntValue;
    BigInteger.prototype.byteValue = bnByteValue;
    BigInteger.prototype.shortValue = bnShortValue;
    BigInteger.prototype.signum = bnSigNum;
    BigInteger.prototype.toByteArray = bnToByteArray;
    BigInteger.prototype.equals = bnEquals;
    BigInteger.prototype.min = bnMin;
    BigInteger.prototype.max = bnMax;
    BigInteger.prototype.and = bnAnd;
    BigInteger.prototype.or = bnOr;
    BigInteger.prototype.xor = bnXor;
    BigInteger.prototype.andNot = bnAndNot;
    BigInteger.prototype.not = bnNot;
    BigInteger.prototype.shiftLeft = bnShiftLeft;
    BigInteger.prototype.shiftRight = bnShiftRight;
    BigInteger.prototype.getLowestSetBit = bnGetLowestSetBit;
    BigInteger.prototype.bitCount = bnBitCount;
    BigInteger.prototype.testBit = bnTestBit;
    BigInteger.prototype.setBit = bnSetBit;
    BigInteger.prototype.clearBit = bnClearBit;
    BigInteger.prototype.flipBit = bnFlipBit;
    BigInteger.prototype.add = bnAdd;
    BigInteger.prototype.subtract = bnSubtract;
    BigInteger.prototype.multiply = bnMultiply;
    BigInteger.prototype.divide = bnDivide;
    BigInteger.prototype.remainder = bnRemainder;
    BigInteger.prototype.divideAndRemainder = bnDivideAndRemainder;
    BigInteger.prototype.modPow = bnModPow;
    BigInteger.prototype.modInverse = bnModInverse;
    BigInteger.prototype.pow = bnPow;
    BigInteger.prototype.gcd = bnGCD;
    BigInteger.prototype.isProbablePrime = bnIsProbablePrime;

    // JSBN-specific extension
    BigInteger.prototype.square = bnSquare;

    // BigInteger interfaces not implemented in jsbn:

    // BigInteger(int signum, byte[] magnitude)
    // double doubleValue()
    // float floatValue()
    // int hashCode()
    // long longValue()
    // static BigInteger valueOf(long val)

    return BigInteger;
});
/**
 * @fileoverview
 * @author 姊у繉 <wuji.xwt@alibaba-inc.com>
 * @module prng4
 **/
KISSY.add('prng4', function (S) {
    // prng4.js - uses Arcfour as a PRNG

    function Arcfour() {
        this.i = 0;
        this.j = 0;
        this.S = new Array();
    }

    // Initialize arcfour context from key, an array of ints, each from [0..255]
    function ARC4init(key) {
        var i, j, t;
        for (i = 0; i < 256; ++i)
            this.S[i] = i;
        j = 0;
        for (i = 0; i < 256; ++i) {
            j = (j + this.S[i] + key[i % key.length]) & 255;
            t = this.S[i];
            this.S[i] = this.S[j];
            this.S[j] = t;
        }
        this.i = 0;
        this.j = 0;
    }

    function ARC4next() {
        var t;
        this.i = (this.i + 1) & 255;
        this.j = (this.j + this.S[this.i]) & 255;
        t = this.S[this.i];
        this.S[this.i] = this.S[this.j];
        this.S[this.j] = t;
        return this.S[(t + this.S[this.i]) & 255];
    }

    Arcfour.prototype.init = ARC4init;
    Arcfour.prototype.next = ARC4next;

    // Plug in your RNG constructor here
    function prng_newstate() {
        return new Arcfour();
    }

    return prng_newstate;
});
/**
 * @fileoverview
 * @author 姊у繉 <wuji.xwt@alibaba-inc.com>
 * @module rng
 **/
KISSY.add('rng', function (S, prng_newstate) {
    // Pool size must be a multiple of 4 and greater than 32.
    // An array of bytes the size of the pool will be passed to init()
    var rng_psize = 256;

    // Random number generator - requires a PRNG backend, e.g. prng4.js

    // For best results, put code like
    // <body onClick='rng_seed_time();' onKeyPress='rng_seed_time();'>
    // in your main HTML document.

    var rng_state;
    var rng_pool;
    var rng_pptr;

    // Mix in a 32-bit integer into the pool
    function rng_seed_int(x) {
        rng_pool[rng_pptr++] ^= x & 255;
        rng_pool[rng_pptr++] ^= (x >> 8) & 255;
        rng_pool[rng_pptr++] ^= (x >> 16) & 255;
        rng_pool[rng_pptr++] ^= (x >> 24) & 255;
        if (rng_pptr >= rng_psize) rng_pptr -= rng_psize;
    }

    // Mix in the current time (w/milliseconds) into the pool
    function rng_seed_time() {
        rng_seed_int(new Date().getTime());
    }

    // Initialize the pool with junk if needed.
    if (rng_pool == null) {
        rng_pool = new Array();
        rng_pptr = 0;
        var t;
        if (window.crypto && window.crypto.getRandomValues) {
            // Use webcrypto if available
            var ua = new Uint8Array(32);
            window.crypto.getRandomValues(ua);
            for (t = 0; t < 32; ++t)
                rng_pool[rng_pptr++] = ua[t];
        }
        if (navigator.appName == "Netscape" && navigator.appVersion < "5" && window.crypto) {
            // Extract entropy (256 bits) from NS4 RNG if available
            var z = window.crypto.random(32);
            for (t = 0; t < z.length; ++t)
                rng_pool[rng_pptr++] = z.charCodeAt(t) & 255;
        }
        while (rng_pptr < rng_psize) { // extract some randomness from Math.random()
            t = Math.floor(65536 * Math.random());
            rng_pool[rng_pptr++] = t >>> 8;
            rng_pool[rng_pptr++] = t & 255;
        }
        rng_pptr = 0;
        rng_seed_time();
        //rng_seed_int(window.screenX);
        //rng_seed_int(window.screenY);
    }

    function rng_get_byte() {
        if (rng_state == null) {
            rng_seed_time();
            rng_state = prng_newstate();
            rng_state.init(rng_pool);
            for (rng_pptr = 0; rng_pptr < rng_pool.length; ++rng_pptr)
                rng_pool[rng_pptr] = 0;
            rng_pptr = 0;
            //rng_pool = null;
        }
        // TODO: allow reseeding after first request
        return rng_state.next();
    }

    function rng_get_bytes(ba) {
        var i;
        for (i = 0; i < ba.length; ++i) ba[i] = rng_get_byte();
    }

    function SecureRandom() {}

    SecureRandom.prototype.nextBytes = rng_get_bytes;

    return SecureRandom;
}, {
    requires: [
        'prng4'
    ]
});
/**
 * @fileoverview
 * @author 姊у繉 <wuji.xwt@alibaba-inc.com>
 * @module rsa
 **/
KISSY.add('index', function (S, BigInteger, SecureRandom) {
    // Depends on jsbn.js and rng.js

    // Version 1.1: support utf-8 encoding in pkcs1pad2

    // convert a (hex) string to a bignum object
    function parseBigInt(str, r) {
        return new BigInteger(str, r);
    }

    // PKCS#1 (type 2, random) pad input string s to n bytes, and return a bigint
    function pkcs1pad2(s, n) {
        if (n < s.length + 11) { // TODO: fix for utf-8
            throw new Error("Message too long for RSA");
            return null;
        }
        var ba = new Array();
        var i = s.length - 1;
        while (i >= 0 && n > 0) {
            var c = s.charCodeAt(i--);
            if (c < 128) { // encode using utf-8
                ba[--n] = c;
            } else if ((c > 127) && (c < 2048)) {
                ba[--n] = (c & 63) | 128;
                ba[--n] = (c >> 6) | 192;
            } else {
                ba[--n] = (c & 63) | 128;
                ba[--n] = ((c >> 6) & 63) | 128;
                ba[--n] = (c >> 12) | 224;
            }
        }
        ba[--n] = 0;
        var rng = new SecureRandom();
        var x = new Array();
        while (n > 2) { // random non-zero pad
            x[0] = 0;
            while (x[0] == 0) rng.nextBytes(x);
            ba[--n] = x[0];
        }
        ba[--n] = 2;
        ba[--n] = 0;
        return new BigInteger(ba);
    }

    // "empty" RSA key constructor
    function RSAKey() {

        // publice key
        this.n = null;

        // encryption exponent
        this.e = 0;

        // private key
        this.d = null;

        this.p = null;
        this.q = null;
        this.dmp1 = null;
        this.dmq1 = null;
        this.coeff = null;
    }

    // Set the public key fields N and e from hex strings
    function RSASetPublic(N, E) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16);
        } else
            throw new Error("Invalid RSA public key");
    }

    // Perform raw public operation on "x": return x^e (mod n)
    function RSADoPublic(x) {
        return x.modPowInt(this.e, this.n);
    }

    // Return the PKCS#1 RSA encryption of "text" as an even-length hex string
    function RSAEncrypt(text) {
        var m = pkcs1pad2(text, (this.n.bitLength() + 7) >> 3);
        if (m == null) return null;
        var c = this._doPublic(m);
        if (c == null) return null;
        var h = c.toString(16);
        if ((h.length & 1) == 0) return h;
        else return "0" + h;
    }

    // Return the PKCS#1 RSA encryption of "text" as a Base64-encoded string
    //function RSAEncryptB64(text) {
    //  var h = this.encrypt(text);
    //  if(h) return hex2b64(h); else return null;
    //}

    // protected
    RSAKey.prototype._doPublic = RSADoPublic;

    // public
    RSAKey.prototype.setPublic = RSASetPublic;
    RSAKey.prototype.encrypt = RSAEncrypt;
    //RSAKey.prototype.encrypt_b64 = RSAEncryptB64;

    // ===================================================
    // Depends on rsa.js and jsbn2.js

    // Version 1.1: support utf-8 decoding in pkcs1unpad2

    // Undo PKCS#1 (type 2, random) padding and, if valid, return the plaintext
    function pkcs1unpad2(d, n) {
        var b = d.toByteArray();
        var i = 0;
        while (i < b.length && b[i] == 0)++i;
        if (b.length - i != n - 1 || b[i] != 2)
            return null;
        ++i;
        while (b[i] != 0)
            if (++i >= b.length) return null;
        var ret = "";
        while (++i < b.length) {
            var c = b[i] & 255;
            if (c < 128) { // utf-8 decode
                ret += String.fromCharCode(c);
            } else if ((c > 191) && (c < 224)) {
                ret += String.fromCharCode(((c & 31) << 6) | (b[i + 1] & 63));
                ++i;
            } else {
                ret += String.fromCharCode(((c & 15) << 12) | ((b[i + 1] & 63) << 6) | (b[i + 2] & 63));
                i += 2;
            }
        }
        return ret;
    }

    // Set the private key fields N, e, and d from hex strings
    function RSASetPrivate(N, E, D) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16);
            this.d = parseBigInt(D, 16);
        } else
            alert("Invalid RSA private key");
    }

    // Set the private key fields N, e, d and CRT params from hex strings
    function RSASetPrivateEx(N, E, D, P, Q, DP, DQ, C) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16);
            this.d = parseBigInt(D, 16);
            this.p = parseBigInt(P, 16);
            this.q = parseBigInt(Q, 16);
            this.dmp1 = parseBigInt(DP, 16);
            this.dmq1 = parseBigInt(DQ, 16);
            this.coeff = parseBigInt(C, 16);
        } else
            throw new Error("Invalid RSA private key");
    }

    // Generate a new random private key B bits long, using public expt E
    function RSAGenerate(B, E) {
        var rng = new SecureRandom();
        var qs = B >> 1;
        this.e = parseInt(E, 16);
        var ee = new BigInteger(E, 16);
        for (;;) {
            for (;;) {
                this.p = new BigInteger(B - qs, 1, rng);
                if (this.p.subtract(BigInteger.ONE).gcd(ee).compareTo(BigInteger.ONE) == 0 && this.p.isProbablePrime(10)) break;
            }
            for (;;) {
                this.q = new BigInteger(qs, 1, rng);
                if (this.q.subtract(BigInteger.ONE).gcd(ee).compareTo(BigInteger.ONE) == 0 && this.q.isProbablePrime(10)) break;
            }
            if (this.p.compareTo(this.q) <= 0) {
                var t = this.p;
                this.p = this.q;
                this.q = t;
            }
            var p1 = this.p.subtract(BigInteger.ONE);
            var q1 = this.q.subtract(BigInteger.ONE);
            var phi = p1.multiply(q1);
            if (phi.gcd(ee).compareTo(BigInteger.ONE) == 0) {
                this.n = this.p.multiply(this.q);
                this.d = ee.modInverse(phi);
                this.dmp1 = this.d.mod(p1);
                this.dmq1 = this.d.mod(q1);
                this.coeff = this.q.modInverse(this.p);
                break;
            }
        }
    }

    // Perform raw private operation on "x": return x^d (mod n)
    function RSADoPrivate(x) {
        if (this.p == null || this.q == null)
            return x.modPow(this.d, this.n);

        // TODO: re-calculate any missing CRT params
        var xp = x.mod(this.p).modPow(this.dmp1, this.p);
        var xq = x.mod(this.q).modPow(this.dmq1, this.q);

        while (xp.compareTo(xq) < 0)
            xp = xp.add(this.p);
        return xp.subtract(xq).multiply(this.coeff).mod(this.p).multiply(this.q).add(xq);
    }

    // Return the PKCS#1 RSA decryption of "ctext".
    // "ctext" is an even-length hex string and the output is a plain string.
    function RSADecrypt(ctext) {
        var c = parseBigInt(ctext, 16);
        var m = this._doPrivate(c);
        if (m == null) return null;
        return pkcs1unpad2(m, (this.n.bitLength() + 7) >> 3);
    }

    // Return the PKCS#1 RSA decryption of "ctext".
    // "ctext" is a Base64-encoded string and the output is a plain string.
    //function RSAB64Decrypt(ctext) {
    //  var h = b64tohex(ctext);
    //  if(h) return this.decrypt(h); else return null;
    //}

    // protected
    RSAKey.prototype._doPrivate = RSADoPrivate;

    // public
    RSAKey.prototype.setPrivate = RSASetPrivate;
    RSAKey.prototype.setPrivateEx = RSASetPrivateEx;
    RSAKey.prototype.generate = RSAGenerate;
    RSAKey.prototype.decrypt = RSADecrypt;
    //RSAKey.prototype.b64_decrypt = RSAB64Decrypt;

    return RSAKey;
}, {
    requires: [
        'jsbn',
        'rng'
    ]
});

function encryptedString(exponent, pbk,  password) {
	  var rsaKeyPair = KISSY.require('index');
	  rsaKeyPair.prototype.setPublic(pbk, exponent);
	  var encryptedPassword = rsaKeyPair.prototype.encrypt(password);
	  return encryptedPassword;
}

//alert(KISSY.require('gallery/rsa/1.0/index'))
//var rsa = KISSY.require('index');
//var pbk = "9a39c3fefeadf3d194850ef3a1d707dfa7bec0609a60bfcc7fe4ce2c615908b9599c8911e800aff684f804413324dc6d9f982f437e95ad60327d221a00a2575324263477e4f6a15e3b56a315e0434266e092b2dd5a496d109cb15875256c73a2f0237c5332de28388693c643c8764f137e28e8220437f05b7659f58c4df94685";
//var exponent = "10001";
//alert(rsa.prototype.setPublic);
//rsa.prototype.setPublic(pbk, exponent);
//var result = rsa.prototype.encrypt("xxxxx");
//alert(result); 
//alert(rsa.prototype.decrypt(rsa)); 