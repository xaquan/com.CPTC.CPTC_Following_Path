package com.CPTC.CPTC_Following_Path.impl;

import java.util.ArrayList;
import java.util.Arrays;

import com.jbm.urcap.sample.scriptCommunicator.communicator.ScriptCommand;
import com.jbm.urcap.sample.scriptCommunicator.communicator.ScriptExporter;
import com.jbm.urcap.sample.scriptCommunicator.communicator.ScriptSender;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;

public class FollowingPathProgramNodeContribution implements ProgramNodeContribution {
	
	private final ProgramAPIProvider apiProvider;
	private final FollowingPathProgramNodeView view;
	private final DataModel model;
	private final UndoRedoManager undoRedoManager;
	
	private static final String OUTPUT_KEY = "output";
	private static final String WIREFEED_SPEED_KEY = "speed";
	private static final String MOVE_SPEED_KEY = "moveSpeed";
	private static final String MOVE_ACCELERATION_KEY = "moveAcceleration";
	private static final String JOINTS_POSITIONS_RECORD = "jointsPositionsRecord";
	private static final String POSE_POSITIONS_RECORD = "posePositionsRecord";
	
	private static final Integer DEFAULT_OUTPUT = 0;
	private static final int DEFAULT_WIREFEED_SPEED = 3;
	private static final int DEFAULT_MOVE_SPEED = 200;
	private static final int DEFAULT_MOVE_ACCELERATION = 1200;

	private final ScriptExporter exporter;
	private final ScriptSender sender;
	
	private final ScriptExporter trackingExporter;
	private final ScriptSender trackingSender;
	
	private boolean isRecording = false; 
	private boolean isFreedrive = false;
	private ArrayList<String> jointsPositionsRecord = new ArrayList<String>();
	private ArrayList<String> posePositionsRecord = new ArrayList<String>();
	
	private int[] freedriveFreeAxes = {1,1,1,1,1,1};
	
	Thread recording;
	boolean isTracking = false;
	Thread tracking;

	public FollowingPathProgramNodeContribution(ProgramAPIProvider apiProvider, FollowingPathProgramNodeView view,
			DataModel model) {
		this.apiProvider = apiProvider;
		this.view = view;
		this.model = model;
		this.undoRedoManager = this.apiProvider.getProgramAPI().getUndoRedoManager();
		

		this.exporter = new ScriptExporter();
		this.sender = new ScriptSender();
		
		this.trackingExporter = new ScriptExporter();
		this.trackingSender = new ScriptSender();
	}
	
	
	public void onOutputSelection(final Integer output) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(OUTPUT_KEY, output);
			}
		});
	}
	
	public void onMoveAccelerationChange(final int val) {
		undoRedoManager.recordChanges(new UndoableChanges(	) {
			
			@Override
			public void executeChanges() {
				model.set(MOVE_ACCELERATION_KEY, val);
			}
		});
	}
	
	public void onMoveSpeedChange(final int val) {
		undoRedoManager.recordChanges(new UndoableChanges(	) {
			
			@Override
			public void executeChanges() {
				model.set(MOVE_SPEED_KEY, val);
			}
		});
	}
	
	public void onSpeedSelection(final int speed) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(WIREFEED_SPEED_KEY, speed);
			}
		});
		
	}
	
	public void oneFreedriveAxesChange(final int[] freeAxes) {
		freedriveFreeAxes = freeAxes;
	}
	
//	private float getJointSpeed() {
//		return model.get(JOINT_SPEED_KEY, DEFAULT_JOINT_SPEED);
//	}
//	
//	private float getJointAcceleration() {
//		return model.get(JOINT_ACCELERATION_KEY, DEFAULT_JOINT_ACCELERATION);
//	}
	
	private int getMoveSpeed() {
		return model.get(MOVE_SPEED_KEY, DEFAULT_MOVE_SPEED);
	}
	
	private int getMoveAcceleration() {
		return model.get(MOVE_ACCELERATION_KEY, DEFAULT_MOVE_ACCELERATION);
	}
	
	private Integer getOutPut() {
		return model.get(OUTPUT_KEY, DEFAULT_OUTPUT);
	}
	
	private int getSpeed() {
		return model.get(WIREFEED_SPEED_KEY, DEFAULT_WIREFEED_SPEED);
	}
	
	private String[] getJointsPositionRecord() {
		return model.get(JOINTS_POSITIONS_RECORD, new String[0]);
	}
	
	private String[] getPosePositionsRecord() {
		return model.get(JOINTS_POSITIONS_RECORD, new String[0]);
	}
	
	private Integer[] getOutputItems() {
		Integer[] items = new Integer[8];
		for (int i = 0; i < 8; i++) {
			items[i] = i;
		}
		return items;
	}
		
	public String getCurrentPose() {
		ScriptCommand urScriptCmd = new ScriptCommand("getCurrentPose");
		urScriptCmd.appendLine("pose_positions = get_actual_tcp_pose()");
		final String res = exporter.exportStringFromURScript(urScriptCmd, "pose_positions");
		return res;
	}
	
	public String getCurrentJointsPosition() {
		ScriptCommand urScriptCmd = new ScriptCommand("getCurrentJoints");
		urScriptCmd.appendLine("joints_positions = get_actual_joint_positions()");
		final String res = exporter.exportStringFromURScript(urScriptCmd, "joints_positions");
		return res;
	}
	
	public void startRecordPath() {
		isRecording = true;
		jointsPositionsRecord.clear();
		posePositionsRecord.clear();
//		freeDriveToggle(null);
		recording = new Thread() {			
			
			@Override
			public void run() {

				String lastPositions = "";
				String lastPose = "";
				while(isRecording) {
					String joints = getCurrentJointsPosition();
					String pose = getCurrentPose();
					
					System.out.println("joint = " + joints);
					System.out.println("pose = " + pose);
					
					if(!joints.equals(lastPositions) && joints != "") {
						jointsPositionsRecord.add(joints);
						lastPositions = joints;
					}
					
					if(!pose.equals(lastPose) && pose != "") {
						posePositionsRecord.add(pose);
						lastPose= pose;
					}
				}
			}
		};		
		recording.start();		
	}
	
	public void stopRecording() {
		isRecording = false;
//		freeDriveToggle(null);
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				int size = jointsPositionsRecord.size();
				String[] jointsRecord = jointsPositionsRecord.toArray(new String[size]);
				String[] poseRecord = posePositionsRecord.toArray(new String[size]);
				model.set(JOINTS_POSITIONS_RECORD, jointsRecord);
				model.set(POSE_POSITIONS_RECORD, poseRecord);
				setPositionsRecordToView();
				
			}
		});
	}
	
//	public void freeDriveToggle(int[] axes) {
//		String strAxes = Arrays.toString(axes);
//		ScriptCommand cmd = new ScriptCommand("freeDriveToggle");
//		if(isFreedrive) {
//			isFreedrive = false;
//			cmd.appendLine("end_freedrive_mode()");
//		}else {			
//			isFreedrive = true;
////			cmd.appendLine("freedrive_mode(freeAxes=" + strAxes + ", feature =\"base\")");
////			cmd.appendLine("freedrive_mode()");
//			cmd.appendLine("status = 1");
//		}		
//		final String status = exporter.exportStringFromURScript(cmd, "status");
//		System.out.println(status);
////		sender.sendScriptCommand(cmd);
//	}
	
//	public void clearRecord() {
//		isRecording = false;
//		jointsPositionsRecord.clear();
//	}
	
	public void tracking() {
		if(isTracking){
			isTracking = false;
			System.out.println("Stop tracking");
		}else {
			isTracking = true;
			tracking = new Thread() {
				
				@Override
				public void run() {
					while(isTracking) {
						ScriptCommand urScriptCmd = new ScriptCommand("getTargetSpeed");
						urScriptCmd.appendLine("tcpSpeed = get_actual_tcp_speed()");
						final String res = trackingExporter.exportStringFromURScript(urScriptCmd, "tcpSpeed");
						System.out.println("Pose speed: " + res);
						
//						ScriptCommand freedriveScriptCmd = new ScriptCommand("getfreedrive");
//						freedriveScriptCmd.appendLine("freedriveStatus = get_freedrive_status()");
//						final String resFreedrive = exporter.exportStringFromURScript(freedriveScriptCmd, "freedriveStatus");
//						System.out.println("Freedrive: " + resFreedrive);
					}
				}
			};
			tracking.start();
		}
	}
		
	
	private void setPositionsRecordToView() {
		if(getJointsPositionRecord().length != 0 ) {
			view.setRecordPathDesc(getJointsPositionRecord().length + " positions.");
		}else {
			view.setRecordPathDesc("No Record!");
		}
	}
	
	@Override
	public void openView() {
		view.setIOComboBoxItems(getOutputItems());
		
		view.setIOComboBoxSelection(getOutPut());
		view.setSpeedSlider(getSpeed());
		
		view.setToolSpeed(getMoveSpeed());
		view.setToolAcceleration(getMoveAcceleration());
		
		setPositionsRecordToView();
	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		return "Path record";
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void generateScript(ScriptWriter writer) {
//		float analogValue =  (float)getSpeed()/250;
//		System.out.println(analogValue);
//		writer.appendLine("set_standard_analog_out(" + getOutPut() + ", " + analogValue + ")");
//		writer.appendLine("movel(" + model.get("StartPoint", "") +", 60, 80, 0)");
		
		
		String[] path = getPosePositionsRecord();
		for(String pos: path) {
			
//			writer.appendLine("movel(" + (String)obj +", "+ (float)72/(float)getJointSpeed() + ", " + (float)20/(float)getJointAcceleration() + ", 0)");
			String cmd = "movel(" + pos +", "+ (float)getMoveSpeed()/(float)60 + ", " + (float)getMoveAcceleration()/(float)60/(float)60 + ", 0, 0)";
			writer.appendLine(cmd);
			System.out.println(cmd);
		}
		
	}

}
