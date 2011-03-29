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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.EventListenerList;
import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;


public class Entity implements IEntity {
	
	//private NodeReference nodeReference;
	private boolean isDisposed=false;
	private boolean isLocked=false;
	
	private SimpleDateFormat dateformaterIn=new SimpleDateFormat("yyyymmdd.hhmmss");
	private SimpleDateFormat dateformaterOut=new SimpleDateFormat("yyyymmdd.hhmmss");
	//protected IEntity ientity;
	
	protected Global global;
	
	protected Global getGlobal() {return global;}
	
	/// Create a new Entity from scratch
	public Entity()
	{
		setGlobal();	
	}
	
	/// Called for Entities that implement IEntity
	/*protected Entity(Global global)
	{
		this.global=global;
	}
	*/
	
	/// By default entities are not locked
	/// Called by class that extend class Entity
	public Entity(long identity)
	throws InstanceNotFoundException
	{
		setGlobal();
		init(identity);
	}
	
	/// Called for Entities that implement IEntity
	/*protected Entity(Global global, long identity)
	throws InstanceNotFoundException
	{
		this.global=global;
		init(identity);
	}
	*/
	/// Constructor to open object existing object
	/// with a given Id and possibly with exclusive access
	/// public Entity(Global global, long identity, boolean lock, IEntity entity)
	/// Called by class that extend class Entity
	public Entity(long identity, boolean lock)
	throws InstanceNotFoundException,InstanceNotLockedException
	{
		setGlobal();
		init(identity, lock);
	}
	
	/// Called for Entities that implement IEntity
	/*protected Entity(Global global, long identity, boolean lock)
	throws InstanceNotFoundException,InstanceNotLockedException
	{
		this.global=global;
		init(identity, lock);
	}
	*/
	
	protected void setGlobal(){}
	
	protected final void init(long identity)
	throws InstanceNotFoundException {
		if (identity>0) {
		global.setIdentity(identity);
		
	// validate that this is a saved object
	// The global reference must be defined and / or
	// have defined subnodes (depends on storage 
	// stratergy of the entity
	if (!global.Exists()) throw new InstanceNotFoundException();
	}
}
	
	protected final void init(long identity, boolean lock)
			throws InstanceNotFoundException, InstanceNotLockedException {
		if (identity>0) {
			global.setIdentity(identity);
			
			// validate that this is a saved object
			// The global reference must be defined and / or
			// have defined subnodes (depends on storage 
			// stratergy of the entity
			if (!global.Exists()) throw new InstanceNotFoundException();
			
			if (lock)
			{
				if (!global.AddLock()) throw new InstanceNotLockedException();
				this.isLocked=true;
			}
		}
	}
	
	
	
	/// It is a bit lazy to have the garbage
	/// collector have to clean up
	/// but is useful to have
	protected void finalize() throws Throwable
	{
	   if (this.isDisposed==true) return;
	   if (this.isLocked)
	   {
		   try
		   {
			   global.RemoveLock();
			   
			   this.isLocked=false;
		   }
		   catch (Exception e)
		   {
			   
		   }
	   }
	}
	
	public final void Close()
	{
		if (this.isLocked)
		   {
			   try
			   {
				   global.RemoveLock();
				   this.isLocked=false;
			   }
			   catch (Exception e)
			   {
				   
			   }
		   }
		this.isDisposed=true;
	}
	
	/// The unique identity of this saved object
	public final long getIdentity()
	{
		return this.global.getIdentity();
	}
	
	public final boolean Validate(List<String> errors)
	{
		return OnValidate(errors);
	}
	
	/// Returns true if the Save succeeds
	public final boolean Save()
	{
		// if validation for this type fails
		// don't proceed with the save
		//if (!Validate(new ArrayList<String>())) return false;
		if (!OnValidate(new ArrayList<String>())) return false;
		Connection connection=ConnectionContext.getConnection();
		/// Increment outside of transaction
		if (this.global.getIdentity()==0) this.global.NewIdentity();
		
		connection.startTransaction();
		try
		{
			OnSave();
			connection.commit();
		} catch (Exception e)
		{
			connection.rollback(connection.transactionLevel());
			return false;
		}
		return true;
	}

	/// Override the OnSave method to persist instance information to database
	/// Use utility methods like SetStringProperty
	/// As convention always call the corresponding "super()" method
	/// to facilitate building up composite types
	protected void OnSave() {}
	
	/// Override the OnValidate method to add validation behaviour
	/// Add distinct error messages to the validationError list
	/// Return false to indicate validation failure
	/// As convention always call the corresponding "super()" method
	/// to facilitate building up composite types
	protected boolean OnValidate(List<String> validationErrors) {return true;}
	
	public final synchronized static boolean Initialise()
	{
		Connection connection = ConnectionContext.getConnection();
		if (!connection.isConnected()) {
            connection.connect("User", "_SYSTEM", "SYS");
         }
		return true;
	}
	
	public final synchronized static void Stop()
	{
		Connection connection = ConnectionContext.getConnection();
		connection.close();
		return;
	}
	
	protected final String GetStringProperty(String name)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		return nodeReference.getString();
	}
	protected final String GetStringProperty(int nametoken)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		return nodeReference.getString();
	}
	protected synchronized final Date GetDateProperty(String name)
	{
		Date ret=null;
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		String sdate=nodeReference.getString();
		if (sdate==null) return ret;
		try{
			ret=dateformaterOut.parse(sdate);
		} catch (ParseException e) {}
		return ret;
	}
	
	protected synchronized final Date GetDateProperty(int nametoken)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		String sdate=nodeReference.getString();
		try{
			return dateformaterOut.parse(sdate);
		} catch (ParseException e)
		{
			return null;
		}
	}
	protected final void SetStringProperty(String name,String value)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		nodeReference.set(value);
	}
	protected final void SetStringProperty(int nametoken,String value)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		nodeReference.set(value);
	}
	protected synchronized final void SetDateProperty(String name,Date date)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		String value=dateformaterIn.format(date);
		nodeReference.set(value);
	}
	protected synchronized final void SetDateProperty(int nametoken,Date date)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		String value=dateformaterIn.format(date);
		nodeReference.set(value);
	}

}
