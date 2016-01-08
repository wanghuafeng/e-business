var window = new Object;
(function (ab) {
    var ad = 2;
    var I = 16;
    var o = I;
    var Q = 1 << 16;
    var e = Q >>> 1;
    var M = Q * Q;
    var T = Q - 1;
    var Z = 9999999999999998;
    var U;
    var aa;
    var n, c;

    function u(af) {
        U = af;
        aa = new Array(U);
        for (var a = 0; a < aa.length; a++) {
            aa[a] = 0
        }
        n = new b();
        c = new b();
        c.digits[0] = 1
    }
    u(20);
    var J = 15;
    var L = q(1000000000000000);

    function b(a) {
        if (typeof a == "boolean" && a == true) {
            this.digits = null
        } else {
            this.digits = aa.slice(0)
        }
        this.isNeg = false
    }

    function r(ai) {
        var ah = ai.charAt(0) == "-";
        var ag = ah ? 1 : 0;
        var a;
        while (ag < ai.length && ai.charAt(ag) == "0") {
            ++ag
        }
        if (ag == ai.length) {
            a = new b()
        } else {
            var af = ai.length - ag;
            var aj = af % J;
            if (aj == 0) {
                aj = J
            }
            a = q(Number(ai.substr(ag, aj)));
            ag += aj;
            while (ag < ai.length) {
                a = g(ae(a, L), q(Number(ai.substr(ag, J))));
                ag += J
            }
            a.isNeg = ah
        }
        return a
    }

    function P(af) {
        var a = new b(true);
        a.digits = af.digits.slice(0);
        a.isNeg = af.isNeg;
        return a
    }

    function q(ag) {
        var a = new b();
        a.isNeg = ag < 0;
        ag = Math.abs(ag);
        var af = 0;
        while (ag > 0) {
            a.digits[af++] = ag & T;
            ag = Math.floor(ag / Q)
        }
        return a
    }

    function x(ag) {
        var a = "";
        for (var af = ag.length - 1; af > -1; --af) {
            a += ag.charAt(af)
        }
        return a
    }
    var d = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");

    function O(ag, ai) {
        var af = new b();
        af.digits[0] = ai;
        var ah = w(ag, af);
        var a = d[ah[1].digits[0]];
        while (f(ah[0], n) == 1) {
            ah = w(ah[0], af);
            digit = ah[1].digits[0];
            a += d[ah[1].digits[0]]
        }
        return (ag.isNeg ? "-" : "") + x(a)
    }

    function ac(ag) {
        var af = new b();
        af.digits[0] = 10;
        var ah = w(ag, af);
        var a = String(ah[1].digits[0]);
        while (f(ah[0], n) == 1) {
            ah = w(ah[0], af);
            a += String(ah[1].digits[0])
        }
        return (ag.isNeg ? "-" : "") + x(a)
    }
    var m = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f");

    function R(ag) {
        var af = 15;
        var a = "";
        for (i = 0; i < 4; ++i) {
            a += m[ag & af];
            ag >>>= 4
        }
        return x(a)
    }

    function B(af) {
        var a = "";
        var ah = V(af);
        for (var ag = V(af); ag > -1; --ag) {
            a += R(af.digits[ag])
        }
        return a
    }

    function A(al) {
        var ag = 48;
        var af = ag + 9;
        var ah = 97;
        var ak = ah + 25;
        var aj = 65;
        var ai = 65 + 25;
        var a;
        if (al >= ag && al <= af) {
            a = al - ag
        } else {
            if (al >= aj && al <= ai) {
                a = 10 + al - aj
            } else {
                if (al >= ah && al <= ak) {
                    a = 10 + al - ah
                } else {
                    a = 0
                }
            }
        }
        return a
    }

    function K(ah) {
        var af = 0;
        var a = Math.min(ah.length, 4);
        for (var ag = 0; ag < a; ++ag) {
            af <<= 4;
            af |= A(ah.charCodeAt(ag))
        }
        return af
    }

    function W(ai) {
        var af = new b();
        var a = ai.length;
        for (var ah = a, ag = 0; ah > 0; ah -= 4, ++ag) {
            af.digits[ag] = K(ai.substr(Math.max(ah - 4, 0), Math.min(ah, 4)))
        }
        return af
    }

    function C(am, al) {
        var a = am.charAt(0) == "-";
        var ah = a ? 1 : 0;
        var an = new b();
        var af = new b();
        af.digits[0] = 1;
        for (var ag = am.length - 1; ag >= ah; ag--) {
            var ai = am.charCodeAt(ag);
            var aj = A(ai);
            var ak = k(af, aj);
            an = g(an, ak);
            af = k(af, al)
        }
        an.isNeg = a;
        return an
    }

    function D(a) {
        return (a.isNeg ? "-" : "") + a.digits.join(" ")
    }

    function g(af, aj) {
        var a;
        if (af.isNeg != aj.isNeg) {
            aj.isNeg = !aj.isNeg;
            a = S(af, aj);
            aj.isNeg = !aj.isNeg
        } else {
            a = new b();
            var ai = 0;
            var ah;
            for (var ag = 0; ag < af.digits.length; ++ag) {
                ah = af.digits[ag] + aj.digits[ag] + ai;
                a.digits[ag] = ah % Q;
                ai = Number(ah >= Q)
            }
            a.isNeg = af.isNeg
        }
        return a
    }

    function S(af, aj) {
        var a;
        if (af.isNeg != aj.isNeg) {
            aj.isNeg = !aj.isNeg;
            a = g(af, aj);
            aj.isNeg = !aj.isNeg
        } else {
            a = new b();
            var ai, ah;
            ah = 0;
            for (var ag = 0; ag < af.digits.length; ++ag) {
                ai = af.digits[ag] - aj.digits[ag] + ah;
                a.digits[ag] = ai % Q;
                if (a.digits[ag] < 0) {
                    a.digits[ag] += Q
                }
                ah = 0 - Number(ai < 0)
            }
            if (ah == -1) {
                ah = 0;
                for (var ag = 0; ag < af.digits.length; ++ag) {
                    ai = 0 - a.digits[ag] + ah;
                    a.digits[ag] = ai % Q;
                    if (a.digits[ag] < 0) {
                        a.digits[ag] += Q
                    }
                    ah = 0 - Number(ai < 0)
                }
                a.isNeg = !af.isNeg
            } else {
                a.isNeg = af.isNeg
            }
        }
        return a
    }

    function V(af) {
        var a = af.digits.length - 1;
        while (a > 0 && af.digits[a] == 0) {
            --a
        }
        return a
    }

    function H(ag) {
        var ai = V(ag);
        var ah = ag.digits[ai];
        var af = (ai + 1) * o;
        var a;
        for (a = af; a > af - o; --a) {
            if ((ah & 32768) != 0) {
                break
            }
            ah <<= 1
        }
        return a
    }

    function ae(ak, aj) {
        var an = new b();
        var ai;
        var af = V(ak);
        var am = V(aj);
        var al, a, ag;
        for (var ah = 0; ah <= am; ++ah) {
            ai = 0;
            ag = ah;
            for (j = 0; j <= af; ++j, ++ag) {
                a = an.digits[ag] + ak.digits[j] * aj.digits[ah] + ai;
                an.digits[ag] = a & T;
                ai = a >>> I
            }
            an.digits[ah + af + 1] = ai
        }
        an.isNeg = ak.isNeg != aj.isNeg;
        return an
    }

    function k(a, aj) {
        var ai, ah, ag;
        result = new b();
        ai = V(a);
        ah = 0;
        for (var af = 0; af <= ai; ++af) {
            ag = result.digits[af] + a.digits[af] * aj + ah;
            result.digits[af] = ag & T;
            ah = ag >>> I
        }
        result.digits[1 + ai] = ah;
        return result
    }

    function v(ai, al, ag, ak, aj) {
        var a = Math.min(al + aj, ai.length);
        for (var ah = al, af = ak; ah < a; ++ah, ++af) {
            ag[af] = ai[ah]
        }
    }
    var p = new Array(0, 32768, 49152, 57344, 61440, 63488, 64512, 65024, 65280, 65408, 65472, 65504, 65520, 65528, 65532, 65534, 65535);

    function t(af, al) {
        var ah = Math.floor(al / o);
        var a = new b();
        v(af.digits, 0, a.digits, ah, a.digits.length - ah);
        var ak = al % o;
        var ag = o - ak;
        for (var ai = a.digits.length - 1, aj = ai - 1; ai > 0; --ai, --aj) {
            a.digits[ai] = ((a.digits[ai] << ak) & T) | ((a.digits[aj] & p[ak]) >>> (ag))
        }
        a.digits[0] = ((a.digits[ai] << ak) & T);
        a.isNeg = af.isNeg;
        return a
    }
    var E = new Array(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535);

    function l(af, al) {
        var ag = Math.floor(al / o);
        var a = new b();
        v(af.digits, ag, a.digits, 0, af.digits.length - ag);
        var aj = al % o;
        var ak = o - aj;
        for (var ah = 0, ai = ah + 1; ah < a.digits.length - 1; ++ah, ++ai) {
            a.digits[ah] = (a.digits[ah] >>> aj) | ((a.digits[ai] & E[aj]) << ak)
        }
        a.digits[a.digits.length - 1] >>>= aj;
        a.isNeg = af.isNeg;
        return a
    }

    function y(af, ag) {
        var a = new b();
        v(af.digits, 0, a.digits, ag, a.digits.length - ag);
        return a
    }

    function h(af, ag) {
        var a = new b();
        v(af.digits, ag, a.digits, 0, a.digits.length - ag);
        return a
    }

    function N(af, ag) {
        var a = new b();
        v(af.digits, 0, a.digits, 0, ag);
        return a
    }

    function f(a, ag) {
        if (a.isNeg != ag.isNeg) {
            return 1 - 2 * Number(a.isNeg)
        }
        for (var af = a.digits.length - 1; af >= 0; --af) {
            if (a.digits[af] != ag.digits[af]) {
                if (a.isNeg) {
                    return 1 - 2 * Number(a.digits[af] > ag.digits[af])
                } else {
                    return 1 - 2 * Number(a.digits[af] < ag.digits[af])
                }
            }
        }
        return 0
    }

    function w(aj, ai) {
        var a = H(aj);
        var ah = H(ai);
        var ag = ai.isNeg;
        var ao, an;
        if (a < ah) {
            if (aj.isNeg) {
                ao = P(c);
                ao.isNeg = !ai.isNeg;
                aj.isNeg = false;
                ai.isNeg = false;
                an = S(ai, aj);
                aj.isNeg = true;
                ai.isNeg = ag
            } else {
                ao = new b();
                an = P(aj)
            }
            return new Array(ao, an)
        }
        ao = new b();
        an = aj;
        var al = Math.ceil(ah / o) - 1;
        var ak = 0;
        while (ai.digits[al] < e) {
            ai = t(ai, 1);
            ++ak;
            ++ah;
            al = Math.ceil(ah / o) - 1
        }
        an = t(an, ak);
        a += ak;
        var ar = Math.ceil(a / o) - 1;
        var ax = y(ai, ar - al);
        while (f(an, ax) != -1) {
            ++ao.digits[ar - al];
            an = S(an, ax)
        }
        for (var av = ar; av > al; --av) {
            var am = (av >= an.digits.length) ? 0 : an.digits[av];
            var aw = (av - 1 >= an.digits.length) ? 0 : an.digits[av - 1];
            var au = (av - 2 >= an.digits.length) ? 0 : an.digits[av - 2];
            var at = (al >= ai.digits.length) ? 0 : ai.digits[al];
            var af = (al - 1 >= ai.digits.length) ? 0 : ai.digits[al - 1];
            if (am == at) {
                ao.digits[av - al - 1] = T
            } else {
                ao.digits[av - al - 1] = Math.floor((am * Q + aw) / at)
            }
            var aq = ao.digits[av - al - 1] * ((at * Q) + af);
            var ap = (am * M) + ((aw * Q) + au);
            while (aq > ap) {
                --ao.digits[av - al - 1];
                aq = ao.digits[av - al - 1] * ((at * Q) | af);
                ap = (am * Q * Q) + ((aw * Q) + au)
            }
            ax = y(ai, av - al - 1);
            an = S(an, k(ax, ao.digits[av - al - 1]));
            if (an.isNeg) {
                an = g(an, ax);
                --ao.digits[av - al - 1]
            }
        }
        an = l(an, ak);
        ao.isNeg = aj.isNeg != ag;
        if (aj.isNeg) {
            if (ag) {
                ao = g(ao, c)
            } else {
                ao = S(ao, c)
            }
            ai = l(ai, ak);
            an = S(ai, an)
        }
        if (an.digits[0] == 0 && V(an) == 0) {
            an.isNeg = false
        }
        return new Array(ao, an)
    }

    function Y(a, af) {
        return w(a, af)[0]
    }

    function z(a, af) {
        return w(a, af)[1]
    }

    function s(af, ag, a) {
        return z(ae(af, ag), a)
    }

    function G(ag, ai) {
        var af = c;
        var ah = ag;
        while (true) {
            if ((ai & 1) != 0) {
                af = ae(af, ah)
            }
            ai >>= 1;
            if (ai == 0) {
                break
            }
            ah = ae(ah, ah)
        }
        return af
    }

    function F(ah, ak, ag) {
        var af = c;
        var ai = ah;
        var aj = ak;
        while (true) {
            if ((aj.digits[0] & 1) != 0) {
                af = s(af, ai, ag)
            }
            aj = l(aj, 1);
            if (aj.digits[0] == 0 && V(aj) == 0) {
                break
            }
            ai = s(ai, ai, ag)
        }
        return af
    }
    var X = {
        setMaxDigits: u,
        biCopy: P,
        biHighIndex: V,
        BigInt: b,
        biDivide: Y,
        biDivideByRadixPower: h,
        biMultiply: ae,
        biModuloByRadixPower: N,
        biSubtract: S,
        biAdd: g,
        biCompare: f,
        biShiftRight: l,
        biFromHex: W,
        biToHex: B,
        biToString: O,
        biFromString: C
    };
    ab.BigTools = ab.BigTools || X
})(window);
(function (c) {
    var h = c.BigTools,
        a = h.BigInt,
        e = h.biFromHex,
        f = h.biHighIndex;

    function d(k) {
        this.modulus = h.biCopy(k);
        this.k = f(this.modulus) + 1;
        var l = new a();
        l.digits[2 * this.k] = 1;
        this.mu = h.biDivide(l, this.modulus);
        this.bkplus1 = new a();
        this.bkplus1.digits[this.k + 1] = 1;
        this.modulo = function (u) {
            var t = h.biDivideByRadixPower(u, this.k - 1);
            var q = h.biMultiply(t, this.mu);
            var p = h.biDivideByRadixPower(q, this.k + 1);
            var o = h.biModuloByRadixPower(u, this.k + 1);
            var v = h.biMultiply(p, this.modulus);
            var n = h.biModuloByRadixPower(v, this.k + 1);
            var m = h.biSubtract(o, n);
            if (m.isNeg) {
                m = h.biAdd(m, this.bkplus1)
            }
            var s = h.biCompare(m, this.modulus) >= 0;
            while (s) {
                m = h.biSubtract(m, this.modulus);
                s = h.biCompare(m, this.modulus) >= 0
            }
            return m
        };
        this.multiplyMod = function (m, o) {
            var n = h.biMultiply(m, o);
            return this.modulo(n)
        };
        this.powMod = function (n, q) {
            var m = new a();
            m.digits[0] = 1;
            var o = n;
            var p = q;
            while (true) {
                if ((p.digits[0] & 1) != 0) {
                    m = this.multiplyMod(m, o)
                }
                p = h.biShiftRight(p, 1);
                if (p.digits[0] == 0 && f(p) == 0) {
                    break
                }
                o = this.multiplyMod(o, o)
            }
            return m
        }
    }

    function g(l, m, k) {
        this.e = e(l);
        this.d = e(m);
        this.m = e(k);
        this.chunkSize = 2 * f(this.m);
        this.radix = 16;
        this.barrett = new d(this.m)
    }

    function b(k) {
        return (k < 10 ? "0" : "") + String(k)
    }
    g.encryptedString = function (t, w) {
        var r = new Array();
        var l = w.length;
        var p = 0;
        while (p < l) {
            r[p] = w.charCodeAt(p);
            p++
        }
        while (r.length % t.chunkSize != 0) {
            r[p++] = 0
        }
        var q = r.length;
        var x = "";
        var o, n, m;
        for (p = 0; p < q; p += t.chunkSize) {
            m = new a();
            o = 0;
            for (n = p; n < p + t.chunkSize; ++o) {
                m.digits[o] = r[n++];
                m.digits[o] += r[n++] << 8
            }
            var v = t.barrett.powMod(m, t.e);
            var u = t.radix == 16 ? h.biToHex(v) : h.biToString(v, t.radix);
            x += u + " "
        }
        return x.substring(0, x.length - 1)
    };
    g.decryptedString = function (o, p) {
        var r = p.split(" ");
        var k = "";
        var n, m, q;
        for (n = 0; n < r.length; ++n) {
            var l;
            if (o.radix == 16) {
                l = e(r[n])
            } else {
                l = h.biFromString(r[n], o.radix)
            }
            q = o.barrett.powMod(l, o.d);
            for (m = 0; m <= f(q); ++m) {
                k += String.fromCharCode(q.digits[m] & 255, q.digits[m] >> 8)
            }
        }
        if (k.charCodeAt(k.length - 1) == 0) {
            k = k.substring(0, k.length - 1)
        }
        return k
    };
    c.RSAKeyPair = c.RSAKeyPair || g
})(window);

window.BigTools.setMaxDigits(130);

function encryptedString(exponent, module,  password) {
	  var rsaKeyPair = new window.RSAKeyPair(exponent, "", module);
	  var encryptedPassword = window.RSAKeyPair.encryptedString(rsaKeyPair, password);
	  return encryptedPassword;
}
