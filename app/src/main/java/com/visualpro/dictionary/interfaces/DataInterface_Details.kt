package com.visualpro.dictionary.interfaces

import com.visualpro.dictionary.model.model_relations.UserWord_DefinitionList_Ref

interface DataInterface_Details {
    fun setWordDetails(list:List<UserWord_DefinitionList_Ref>)
}