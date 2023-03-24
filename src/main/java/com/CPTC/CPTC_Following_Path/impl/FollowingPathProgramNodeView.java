package com.CPTC.CPTC_Following_Path.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Dictionary;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;

public class FollowingPathProgramNodeView implements SwingProgramNodeView<FollowingPathProgramNodeContribution> {
	
	private final ViewAPIProvider apiProvider;
	private ContributionProvider<FollowingPathProgramNodeContribution> provider; 

	public FollowingPathProgramNodeView(ViewAPIProvider apiProvider) {
		this.apiProvider = apiProvider;
	}
	
	private JComboBox<Integer> ioComboBox =  new JComboBox<Integer>();
	private JSlider speedSlider = new JSlider();
	private boolean isRecoding = false;
	
	private JButton btnRecord;
	private JButton btnStop;
	private JButton btnTracking = new JButton("Tracking");
	private JButton btnFreedrive = new JButton("Freedrive");
	
	private JTextField txtMoveSpeed = new JTextField();
	private JTextField txtMoveAcceleration = new JTextField();
	
	private JLabel lblRecordPathDesc = new JLabel("No Record!");
	
	private JCheckBox cbAxisX = new JCheckBox("X", true);
	private JCheckBox cbAxisY = new JCheckBox("Y", true);
	private JCheckBox cbAxisZ = new JCheckBox("Z", true);
	private JCheckBox cbAxisrX = new JCheckBox("rX", true);
	private JCheckBox cbAxisrY = new JCheckBox("rY", true);
	private JCheckBox cbAxisrZ = new JCheckBox("rZ", true);
	
	
	@Override
	public void buildUI(JPanel panel, final ContributionProvider<FollowingPathProgramNodeContribution> provider) {
		this.provider = provider;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(createDescription("Press Record and hold Free Move to record the movement. Press Stop when done."));
		Box boxButtons = Box.createHorizontalBox();
		boxButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		btnRecord = createButton("Record", provider, btnRecordClicked);
		btnRecord.setEnabled(!isRecoding);
		boxButtons.add(btnRecord);
		btnStop = createButton("Stop", provider, btnStopClicked);
		btnStop.setEnabled(false);
		boxButtons.add(btnStop);
		panel.add(boxButtons);
		panel.add(lblRecordPathDesc);
		panel.add(createSpacer(5));	
		
		
		panel.add(createSpacer(5));
		
		panel.add(createInputField("Joint Speed: mm/s",txtMoveSpeed, txtJointSpeedInputListener));		
		panel.add(createInputField("Joint Acceleration: mm/s^2", txtMoveAcceleration, txtJointAccelerationInputListener));
		
//		Box boxAxis = Box.createHorizontalBox();
//		boxAxis.setAlignmentX(Component.LEFT_ALIGNMENT);
//		boxAxis.add(cbAxisX);	
//		boxAxis.add(cbAxisY);
//		boxAxis.add(cbAxisZ);
//		boxAxis.add(cbAxisrX);
//		boxAxis.add(cbAxisrY);
//		boxAxis.add(cbAxisrZ);
//		boxAxis.add(btnFreedrive);
//		setupBoxAxis();
//		panel.add(boxAxis);
		
		btnTracking.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				provider.get().tracking();
			}
		});
		
//		panel.add(btnTracking);
	}
	
	private void setupBoxAxis() {
		cbAxisrX.addChangeListener(cbAxisChange);
		cbAxisrY.addChangeListener(cbAxisChange);
		cbAxisrZ.addChangeListener(cbAxisChange);
		cbAxisX.addChangeListener(cbAxisChange);
		cbAxisY.addChangeListener(cbAxisChange);
		cbAxisZ.addChangeListener(cbAxisChange);
		btnFreedrive.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int[] axes = {
						booleanToInt(cbAxisX.isSelected()), 
						booleanToInt(cbAxisY.isSelected()), 
						booleanToInt(cbAxisZ.isSelected()), 
						booleanToInt(cbAxisrX.isSelected()), 
						booleanToInt(cbAxisrY.isSelected()), 
						booleanToInt(cbAxisrZ.isSelected())
				};
//				provider.get().freeDriveToggle(axes);
			}
		});
	}
	
	private int booleanToInt(boolean b) {
		if(b) {
			return 1;
		}
		return 0;
	}
	
	public void setRecordPathDesc(String desc) {
		lblRecordPathDesc.setText(desc);
	}
	
	public void setIOComboBox(Integer item) {
		ioComboBox.setSelectedIndex(item);		
	}
	
	public void setIOComboBoxItems(Integer[] items) {
		ioComboBox.removeAllItems();
		ioComboBox.setModel(new DefaultComboBoxModel<Integer>(items));
		
	}
	
	public void setIOComboBoxSelection(Integer item) {
		ioComboBox.setSelectedIndex(item);
	}
	
	public void setSpeedSlider(int value) {
		speedSlider.setValue(value);
	}
	
	public void setToolSpeed(int value) {
		txtMoveSpeed.setText(Integer.toString(value));
	}
	
	public void setToolAcceleration(int value) {
		txtMoveAcceleration.setText(Integer.toString(value));
	}
	
	public void setRecordButtonsStatus() {
		if (isRecoding) {
			btnRecord.setEnabled(false);
			btnStop.setEnabled(true);
		} else {
			btnRecord.setEnabled(true);
			btnStop.setEnabled(false);
		}
	}
	
	private Box createInputField(String label, final JTextField txtInput, MouseListener actionListener) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setSize(300, 30);
		
		JLabel lblInput = new JLabel(label);
		
		txtInput.setPreferredSize(new Dimension(100,25));
		txtInput.setMaximumSize(txtInput.getPreferredSize());
		txtInput.addMouseListener(actionListener);
//		txtInput.addInputMethodListener(txtInputInputListener);
		
		box.add(lblInput);
		box.add(txtInput);
		
		return box;
	}
	
	private Box createDescription(String desc) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel label = new JLabel(desc);
		
		box.add(label);
		
		return box;
	}
//	
//	private Box createIOCombobox(final JComboBox<Integer> combo, final ContributionProvider<FollowingPathProgramNodeContribution> provider) {
//		Box box = Box.createHorizontalBox();
//		box.setAlignmentX(Component.LEFT_ALIGNMENT);
//		
//		JLabel label = new JLabel(" analog_out ");
//		
//		combo.setPreferredSize(new Dimension(204, 30));
//		combo.setMaximumSize(combo.getPreferredSize());
//		
//		combo.addItemListener(new ItemListener() {
//			
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange() == ItemEvent.SELECTED) {
//					provider.get().onOutputSelection((Integer) e.getItem());
//				}
//			}
//		});
//		
//		box.add(label);
//		box.add(combo);
//		
//		return box;
//	}
//
//	private Component createSpeedSlider(final JSlider slider, int min, int max, final ContributionProvider<FollowingPathProgramNodeContribution> provider) {
//		Box box = Box.createHorizontalBox();
//		box.setAlignmentX(Component.LEFT_ALIGNMENT);
//		
//		slider.setMinimum(min);
//		slider.setMaximum(max);
//		slider.setOrientation(JSlider.HORIZONTAL);
//		
//		slider.setPreferredSize(new Dimension(275, 30));
//		slider.setMaximumSize(slider.getPreferredSize());
//		
//		final JLabel value = new JLabel(Integer.toString(slider.getValue()) + " s");
//		
//		slider.addChangeListener(new ChangeListener() {
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				int newValue = slider.getValue();
//				value.setText(Integer.toString(newValue) + " s");				
//				provider.get().onSpeedSelection(newValue);
//			}
//		});
//		
//		box.add(slider);
//		box.add(value);
//		
//		return box;
//	}
	
	private Component createSpacer(int height) {
		return Box.createRigidArea(new Dimension(0, height));
	}
	
	private JButton createButton(String name, final ContributionProvider<FollowingPathProgramNodeContribution> provider, ActionListener func) {
//		Box box = Box.createHorizontalBox();
//		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btn = new JButton(name);
		
		btn.addActionListener(func);
		
		return btn;
	}	
	
	
	private ActionListener btnStopClicked = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			isRecoding = false;
			provider.get().stopRecording();
			setRecordButtonsStatus();
		}
	};
	
//	private ActionListener btnClearClicked = new ActionListener() {
//		
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			isRecoding = false;
//			btnRecord.setText("Record");
//			btnClear.setEnabled(false);
//			provider.get().clearRecord();
//		}
//	};
	
	private ActionListener btnRecordClicked = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			isRecoding = true;
			provider.get().startRecordPath();
			setRecordButtonsStatus();
		}
	};
	
	
	
	private MouseListener txtJointSpeedInputListener  = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println(e.getSource().equals(txtMoveSpeed));
			showKeyBoardNumber(txtMoveSpeed);			
		}
	};
	
	private MouseListener txtJointAccelerationInputListener = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			showKeyBoardNumber(txtMoveAcceleration);			
		}
	};
	
	private void showKeyBoardNumber(final JTextField txtInput) {
		apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory().createIntegerKeypadInput().show(txtInput, new KeyboardInputCallback<Integer>() {
			
			@Override
			public void onOk(Integer value) {
				if(value != 0) {
					txtInput.setText(Integer.toString(value));
					if(txtInput == txtMoveSpeed) {
	
						provider.get().onMoveSpeedChange(value);
					}
					
					if(txtInput == txtMoveAcceleration) {
	
						provider.get().onMoveAccelerationChange(value);
					}
				}				
			}
		});
	}
	
	private ChangeListener cbAxisChange = new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {
						
		}
	};
	

}
