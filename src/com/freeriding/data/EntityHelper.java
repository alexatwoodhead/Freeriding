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

import javax.swing.event.EventListenerList;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;

public class EntityHelper implements IEntity {

	//private static Connection connection = ConnectionContext.getConnection();
	
	// OnSave Events
	private EventListenerList onSave = new EventListenerList();
	
	// OnValidate Events
	private EventListenerList onValidate = new EventListenerList();
	
	// OnOpen Events
	private EventListenerList onOpen = new EventListenerList();
	
	//private NodeReference nodeReference;
	private boolean isDisposed=false;
	private boolean isLocked=false;
	//protected IEntity ientity;
	
	private Global global;

	public EntityHelper(Global global)
	{
		this.global=global;
	}
	
	public EntityHelper(Global global, long identity)
	throws InstanceNotFoundException
	{
		this.global=global;
		init(identity);
	}
	
	public EntityHelper(Global global, long identity, boolean lock)
	throws InstanceNotFoundException,InstanceNotLockedException
	{
		this.global=global;
		init(identity, lock);
	}
	
	private void init(long identity)
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
	
	private void init(long identity, boolean lock)
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
	
	public void AddSaveListener(SaveListener saveListener)
	{
		onSave.add(SaveListener.class, saveListener);
	}
	
	public void AddValidateListener(ValidateListener validateListener)
	{
		onValidate.add(ValidateListener.class, validateListener);
	}
	
	public void AddOpenListener(OpenListener openListener)
	{
		onOpen.add(OpenListener.class, openListener);
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
			   onOpen=null;
			   onValidate=null;
			   onSave=null;
			   
			   this.isLocked=false;
		   }
		   catch (Exception e)
		   {
			   
		   }
	   }
	}
	
	public void Close()
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
	public long getIdentity()
	{
		return this.global.getIdentity();
	}
	
	public boolean Validate(List<String> errors)
	{
		///List<String> errors= new List<String>();
		if (onValidate.getListenerCount()==0) return true;
		boolean hasError=false;
		for (Object listener : onValidate.getListenerList())
		{
			if (((ValidateListener)listener).OnValidate(errors))
			{
				hasError=true;
			}
		}
		return hasError;
	}
	
	/// Returns true if the Save succeeds
	public boolean Save()
	{
		// if validation for this type fails
		// don't proceed with the save
		if (!Validate(new ArrayList<String>())) return false;
		
		/// Increment outside of transaction
		if (this.global.getIdentity()==0) this.global.NewIdentity();
		
		Connection connection=ConnectionContext.getConnection();
		connection.startTransaction();
		try
		{
			//this.ientity.OnSave(this.global);
			for (Object listener : onSave.getListenerList())
			{
				((SaveListener)listener).OnSave(this);
			}
			
			connection.commit();
		} catch (Exception e)
		{
			connection.rollback(connection.transactionLevel());
			return false;
		}
		return true;
	}
	
	public boolean Open(long identity)
	{
		return Open(identity,false);
	}
	
	public boolean Open(long identity, boolean lock)
	{
		Connection connection=ConnectionContext.getConnection();
		this.global.setIdentity(identity);
		try
		{
			//this.ientity.OnSave(this.global);
			for (Object listener : onOpen.getListenerList())
			{
				((OpenListener)listener).OnOpen(this);
			}
			
			connection.commit();
		} catch (Exception e)
		{
			connection.rollback(connection.transactionLevel());
			return false;
		}
		return true;
	}
	
	
	protected String GetStringProperty(String name)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		return nodeReference.getString();
	}
	protected String GetStringProperty(int nametoken)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		return nodeReference.getString();
	}
	protected void SetStringProperty(String name,String value)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(name);
		nodeReference.set(value);
	}
	protected void SetStringProperty(int nametoken,String value)
	{
		NodeReference nodeReference = global.getNodeReference();
		nodeReference.appendSubscript(nametoken);
		nodeReference.set(value);
	}

}
