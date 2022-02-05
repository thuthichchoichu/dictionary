package com.visualpro.realproject.repositories

import com.visualpro.myapplication.Model.Definition
import com.visualpro.myapplication.Model.NearByWord
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class HtmlParseHelper(val master: Element) {

    fun parseNearByWord(): ArrayList<NearByWord> {
        val parseList = master.selectFirst("ul[class=list-col]")
        var nearByWordList = ArrayList<NearByWord>(0)
        if (parseList == null) {
            return nearByWordList
        }

        for (i in 0 until parseList.childrenSize()) {
            val e = parseList.child(i)
            if (e.selectFirst("pos-g[hclass=pos][htag=span]") != null) {
                val URLnearByWord = e.child(0).attr("href")
                val type = e.selectFirst("pos-g[hclass=pos][htag=span]")!!.text()
                val stringBuilder = StringBuilder(e.text())
                val word12 =
                    stringBuilder.delete(stringBuilder.length - type.length, stringBuilder.length)
                        .toString()
                nearByWordList.add(NearByWord(word12, URLnearByWord, type))
            } else {
                val URLnearByWord = e.child(0).attr("href")
                nearByWordList.add(NearByWord(e.text(), URLnearByWord, ""))
            }
        }

        return nearByWordList
    }

    fun parseSoundUrl_Pron(): Array<String> {
        val pro = master.selectFirst("span[class=phonetics]")
        var array = arrayOf<String>("", "", "", "")
        if (pro == null) {
            return array
        }
        for (i in 0 until pro.childrenSize()) {
            val e1 = pro.child(i)
            if (e1.hasClass("phons_br")) { //uk
                array.set(1,e1.child(1).text())
                array.set(3, e1.child(0).attr("data-src-mp3"))
            } else if (e1.hasClass("phons_n_am")) { //Us
                array.set(0,e1.child(1).text())
                array.set(2,e1.child(0).attr("data-src-mp3"))

            }
        }
        return array
    }

    fun parseDefinition(): ArrayList<Definition?> {
        val defList = ArrayList<Definition?>()
        var BigGroup = master.selectFirst("ol[class=senses_multiple][htag=ol]")
        if (BigGroup == null) {
            BigGroup = master.selectFirst("ol[class=sense_single][htag=ol]")
        }
        if (BigGroup == null) {
            return defList
        }


        val DefGroup = BigGroup.select("span[class=def][htag=span][hclass=def]")
        val ExGroup = Elements()
        var isFind = false;
        var mode = false

        for (j in 0 until (BigGroup.childrenSize())) {
            var k = 0
            while (k < BigGroup.child(j).childrenSize()) {
                if (!mode) {
                    if (BigGroup.child(j).child(k).hasClass("examples")) {
                        isFind = true
                        ExGroup.add(BigGroup.child(j).child(k))
                        break
                    }
                } else {
                    val e = BigGroup.child(j).child(k)
                    if (e.hasClass("sense")) {
                        for (m in 0 until e.childrenSize()) {
                            if (e.child(m).hasClass("examples")) {
                                isFind = true
                                ExGroup.add(e.child(m))
                                break
                            }
                        }
                    }
                }
                if (k == BigGroup.child(j).childrenSize() - 1 && !isFind && !mode) {
                    k = 0
                    mode = true
                }
                k++
            }
        }
        for (i in DefGroup.indices) {
            val e1 = DefGroup!![i]
            val e2 = if (i < ExGroup.size) ExGroup[i] else Element("g")
            val examples1 = java.util.ArrayList<String>()
            val ex = e2.select("span[class=x]")
            for (e in ex) {
                examples1.add(e.text())
            }
            defList.add(
                Definition(e1.text(), ArrayList(), examples1)
            )
        }
        return defList
    }

    fun parseType(): String {
        val typeE = master.selectFirst("span[class=pos][hclass=pos][htag=span]")
        return if (typeE != null) typeE.text() else ""
    }
}