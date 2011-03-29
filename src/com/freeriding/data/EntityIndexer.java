/*
 * Copyright Alex Woodhead 2011 - alexatwoodhead@gmail.com
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.freeriding.data;

import java.util.ArrayList;
import java.util.List;

import com.intersys.globals.NodeReference;

/// Limitations:
/// * lowercase all words
/// * replace punctuation with "_" character
public class EntityIndexer {
	
	private static Global globalId=new Global("EntityIndexer",new Subscript("words"),new Subscript(0L),new Subscript("Id"));
	
	private static Object lockWI=new Object();
	
	private Global getGlobalWI(String word)
	{
		return new Global("EntityIndexer",new Subscript("words"),new Subscript("wi"),new Subscript(word));
	}
	//^EntityIndexer("words",0,"Id");
	//^EntityIndexer("words","wi",[word])=id
	//^EntityIndexer("words","iw",[id])=word
	//^EntityIndexer("data",classname,field,wordId,EntityId)=""
	
	/// Retrieve the word identity that corresponds to a given word.
	/// Create a new word identity where required
	public static Long GetWordId(String word)
	{
		/// Check to see whether this word has been previously indexed
		/// If so return the previously assigned word identifier
		Global globalWI=new Global("EntityIndexer",new Subscript("words"),new Subscript("wi"),new Subscript(word.toLowerCase()));
		NodeReference nodeReferenceWI=globalWI.getNodeReference();
		if (nodeReferenceWI.exists()) return nodeReferenceWI.getLong();
		
		/// Create a new wordId item
		Long wordId;
		synchronized(lockWI)
		{
			NodeReference nodeReference=globalId.getNodeReference();
			wordId=nodeReference.increment(1);
		}
		nodeReferenceWI.set(wordId);
		
		Global globalIW=new Global("EntityIndexer",new Subscript("words"),new Subscript("iw"),new Subscript(wordId));
		NodeReference nodeReferenceIW=globalIW.getNodeReference();
		nodeReferenceIW.set(word);
		
		return wordId;
	}
	
	/// Retrieve the word that corresponds to a given word identity
	public static String GetWord(Long wordId)
	{
		Global globalIW=new Global("EntityIndexer",new Subscript("words"),new Subscript("iw"),new Subscript(wordId));
		NodeReference nodeReferenceIW=globalIW.getNodeReference();
		return nodeReferenceIW.getString();
	}
	
	/// Associated a given entity identifier with a given list of words
	public static void IndexEntityWords(String classname, String propertyname, String[] words, Long entityId)
	{
		Global global;
		Long wordId;
		if (classname==null) return;
		if (classname.length()==0) return;
		if (propertyname==null) return;
		if (propertyname.length()==0) return;
		for (String word : words)
		{
			if (word==null) continue;
			if (word.length()==0) continue;
			wordId=GetWordId(word.toLowerCase());
			global=new Global("EntityIndexer",new Subscript("data"),new Subscript(classname),new Subscript(propertyname),new Subscript(wordId),new Subscript(entityId));
			NodeReference nodeReference=global.getNodeReference();
			nodeReference.set("");
		}
	}
	
	/// Dissociate a given entity identifier from a given list of words 
	public static void RemoveIndexEntityWords(String classname, String propertyname, String[] words, Long entityId)
	{
		Global global;
		Long wordId;
		if (classname==null) return;
		if (classname.length()==0) return;
		if (propertyname==null) return;
		if (propertyname.length()==0) return;
		for (String word : words)
		{
			if (word==null) continue;
			if (word.length()==0) continue;
			wordId=GetWordId(word.toLowerCase());
			global=new Global("EntityIndexer",new Subscript("data"),new Subscript(classname),new Subscript(propertyname),new Subscript(wordId),new Subscript(entityId));
			NodeReference nodeReference=global.getNodeReference();
			nodeReference.kill();
		}
	
	}

	/// Return a list of identifiers for entities with named properties equal to a given word value
	public static Long[] FindEntities(String classname, String propertyname, String word)
	{
		List<Long> entities=new ArrayList();
		// convert search word to wordId
		Long wordId=GetWordId(word);
		String subscript = "";
		Global global=new Global("EntityIndexer",new Subscript("data"),new Subscript(classname),new Subscript(propertyname),new Subscript(wordId));  //,new Subscript(next)
		NodeReference nodeReference=global.getNodeReference();
		for (;;)
		{
			subscript=nodeReference.nextSubscript(subscript);
			if (subscript.length()==0) break;
			entities.add(Long.parseLong(subscript));
		}
		Long[] ret={};
		return entities.toArray(ret);
	}
}
