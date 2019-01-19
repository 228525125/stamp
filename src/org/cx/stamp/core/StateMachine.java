package org.cx.stamp.core;

public class StateMachine<T> {

	private AbstractState<T> currentState = null;
	private AbstractState<T> previousState = null;
	private T owner = null;
	
	public StateMachine(T owner) {
		// TODO Auto-generated constructor stub
		this.owner = owner;
	}
	
	/**
	 * ×´Ì¬¸üÐÂ
	 */
	public void update(){
		if(null!=this.currentState)
			this.currentState.execute(this.owner);
	}
	
	/**
	 * ÐÞ¸Ä×´Ì¬
	 * @param newState
	 */
	public void changeState(AbstractState<T> newState){
		this.previousState = this.currentState;
		
		if(null!=this.currentState)
			this.currentState.exit(this.owner);		
		
		this.currentState = newState;
		
		this.currentState.enter(this.owner);
	}
	
	public void setCurrentState(AbstractState<T> currentState) {
		this.currentState = currentState;
	}
	
	public AbstractState<T> getCurrentState() {
		return currentState;
	}
	
	public void setPreviousState(AbstractState<T> previousState) {
		this.previousState = previousState;
	}
	
	public AbstractState<T> getPreviousState() {
		return previousState;
	}
}
