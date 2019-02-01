package org.cx.stamp.core;

public abstract class AbstractState<T> {
	
	public final static Integer Code_BlankState = 0;
	public final static Integer Code_WaitState = 1;
	public final static Integer Code_ReadyState = 2;
	public final static Integer Code_RunningState = 3;
	public final static Integer Code_CompleteState = 4;
	public final static Integer Code_OvertimeState = 5;
	public final static Integer Code_IncorrectFormatState = 6;
	
	public abstract Integer getCode();
	
	public void enter(T t) {
		
	}
	
	public void execute(T t) {
		
	}
	
	public void exit(T t) {
		
	}
	
	/*@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if (arg0 instanceof AbstractState) {
			AbstractState<T> state = (AbstractState<T>) arg0;
			if(null!=getCode())
				return getCode().equals(state.getCode());
		}
		return super.equals(arg0);
	}*/
}
