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
package com.freeriding.test;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.freeriding.data.Entity;
import com.freeriding.data.EntityIndexer;
import com.freeriding.test.Person;

public class Test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Entity.Initialise();
		/*Person person=new Person();
		person.setSurname("Smith");
		person.setForename("John");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyymmdd.hhmmss");
	    Date date=sdf.parse("19450102.000000",new ParsePosition(0));
	    person.setDateOfBirth(date);
		person.Save();
		System.out.println(String.format("Person id is '%1$s'",person.getIdentity()));
		
		try
		{
	      Person person=new Person(4);
	      System.out.println(String.format("Person id is '%1$s'",person.getIdentity()));
	      System.out.println(String.format("  Surname: '%1$s'",person.getSurname()));
	      System.out.println(String.format("  Forename: '%1$s'",person.getForename()));
	      System.out.println(String.format("  DateOfBirth: '%1$s'",person.getDateOfBirth()));
	      
	      
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		/*
		SimpleDateFormat sdf=new SimpleDateFormat("yyyymmdd.hhmmss");
		Date date=sdf.parse("20101009.080706",new ParsePosition(0));
		System.out.println(date);
		String string=sdf.format(date);
		System.out.println(string);
		
		String word="ABC";
		System.out.println("The word was "+word);
		Long wordId=EntityIndexer.GetWordId(word);
		System.out.println("The wordId is "+String.valueOf(wordId));
		String wordout=EntityIndexer.GetWord(wordId);
		System.out.println("The wordout is "+wordout);
		*/
		
		List<String> words=new ArrayList<String>();
		words.add("Test1");
		words.add("Test2");
		String[] str={};
		EntityIndexer.IndexEntityWords("classname", "propertyname", words.toArray(str),2L);
		EntityIndexer.IndexEntityWords("classname", "propertyname", words.toArray(str),3L);
		EntityIndexer.IndexEntityWords("classname", "propertyname", words.toArray(str),4L);
		
		//EntityIndexer.RemoveIndexEntityWords("classname","propertyname", words.toArray(str), 2L);
		
		Long[] entities=EntityIndexer.FindEntities("classname","propertyname","Test2");
		System.out.println(entities.length);
		
		Entity.Stop();
	}

}
