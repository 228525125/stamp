package org.cx.stamp.core;

public class WaitState extends AbstractState<Machine> {

	private static WaitState state = null;
	
	public static WaitState getInstance(){
		if(null==state)
			state = new WaitState();
		
		return state;
	}
	
	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		if(t.isReady() && !ReadyState.getInstance().equals(t.getPreviousState())){
			t.changeState(ReadyState.getInstance());
		}else if(t.isOvertime() && !OvertimeState.getInstance().equals(t.getPreviousState())){
			t.changeState(OvertimeState.getInstance());
		}else if(t.isIncorrectFormat() && !IncorrectFormatState.getInstance().equals(t.getPreviousState())){
			t.changeState(IncorrectFormatState.getInstance());
		}else if(t.isRunning() && !RunningState.getInstance().equals(t.getPreviousState())){
			t.changeState(RunningState.getInstance());
		}else if(t.isComplete() && !CompleteState.getInstance().equals(t.getPreviousState())){
			t.changeState(CompleteState.getInstance());
		}
	}
}
