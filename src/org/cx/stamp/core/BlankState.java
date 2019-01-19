package org.cx.stamp.core;

/**
 * 空白状态，用于状态的过渡，例如ReadyState生成编号失败后，不能直接切换到WaitState，
 * 需要空白状态来过渡一下；
 * @author chenxian
 *
 */
public class BlankState extends AbstractState<Machine> {

	private static BlankState state = null;
	
	public static BlankState getInstance(){
		if(null==state)
			state = new BlankState();
		
		return state;
	}
	
	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		t.changeState(WaitState.getInstance());
	}
}
