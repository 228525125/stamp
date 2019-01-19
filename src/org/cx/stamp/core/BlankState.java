package org.cx.stamp.core;

/**
 * �հ�״̬������״̬�Ĺ��ɣ�����ReadyState���ɱ��ʧ�ܺ󣬲���ֱ���л���WaitState��
 * ��Ҫ�հ�״̬������һ�£�
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
