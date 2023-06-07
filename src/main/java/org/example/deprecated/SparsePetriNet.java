package org.example.deprecated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class SparsePetriNet {
	private final SparseIntVector initialMarking;
	private final List<String> transitionNames;
	private final List<String> placeNames;
	private final IntMatrixSparseColumns flowMatrixPT; /* backward */
	private final IntMatrixSparseColumns flowMatrixTP; /* forward */

	public SparsePetriNet() {
		this.initialMarking = new SparseIntVector(0);
		this.transitionNames = new ArrayList<>();
		this.placeNames = new ArrayList<>();
		this.flowMatrixPT = new IntMatrixSparseColumns(0, 0);
		this.flowMatrixTP = new IntMatrixSparseColumns(0, 0);
	}

	public SparsePetriNet(SparsePetriNet original) {
		this.initialMarking = new SparseIntVector(original.initialMarking);
		this.transitionNames = new ArrayList<>(original.transitionNames);
		this.placeNames = new ArrayList<>(original.placeNames);
		this.flowMatrixPT = new IntMatrixSparseColumns(original.flowMatrixPT);
		this.flowMatrixTP = new IntMatrixSparseColumns(original.flowMatrixTP);
	}

	public int addTransition(String name) {
		this.transitionNames.add(name);
		this.flowMatrixPT.appendColumn(new SparseIntVector(0));
		this.flowMatrixTP.appendColumn(new SparseIntVector(0));
		int id = this.transitionNames.size() - 1;
		return id;
	}

	public int addPlace(String name, int initialTokens) {
		this.placeNames.add(name);
		this.flowMatrixPT.addRow();
		this.flowMatrixTP.addRow();
		int id = this.placeNames.size() - 1;
		this.initialMarking.set(id, initialTokens);
		return id;
	}

	public void removeTransition(int transitionId) {
		this.flowMatrixPT.removeColumn(transitionId);
		this.flowMatrixTP.removeColumn(transitionId);
		this.transitionNames.remove(transitionId);
	}

	public void removeTransitions(int... transitionIds) {
		Arrays.sort(transitionIds);
		for (int i = transitionIds.length - 1; i >= 0; i--) {
			this.removeTransition(transitionIds[i]);
		}
	}

	public void removePlace(int placeId) {
		this.flowMatrixPT.removeRow(placeId);
		this.flowMatrixTP.removeRow(placeId);
		this.initialMarking.removeAndDecrementFollowingKeys(placeId);
		this.placeNames.remove(placeId);
	}

	public void removePlaces(int... placeIds) {
		Arrays.sort(placeIds);
		for (int i = placeIds.length - 1; i >= 0; i--) {
			this.removePlace(placeIds[i]);
		}
	}

	public void addPreFlowPT(int placeId, int transitionId, int weight) {
		this.flowMatrixPT.set(placeId, transitionId, weight);
	}

	public void addPostFlowTP(int transitionId, int placeId, int weight) {
		this.flowMatrixTP.set(placeId, transitionId, weight);
	}

	public SparseIntVector getInitialMarking() {
		return this.initialMarking;
	}

	public IntMatrixSparseColumns getFlowMatrixPT() {
		return this.flowMatrixPT;
	}

	public IntMatrixSparseColumns getFlowMatrixTP() {
		return this.flowMatrixTP;
	}

	public int getTransitionCount() {
		return this.transitionNames.size();
	}

	public List<String> getTransitionNames() {
		return this.transitionNames;
	}

	public int getPlaceCount() {
		return this.placeNames.size();
	}

	public List<String> getPlaceNames() {
		return this.placeNames;
	}

	public String getTransitionName(int transitionId) {
		return this.transitionNames.get(transitionId);
	}

	public String getPlaceName(int placeId) {
		return this.placeNames.get(placeId);
	}

	public int getTransitionId(String transitionName) {
		return this.transitionNames.indexOf(transitionName);
	}

	public int getPlaceId(String placeName) {
		return this.placeNames.indexOf(placeName);
	}

	public SparseIntVector presetT(int transitionId) {
		return this.flowMatrixPT.getColumns().get(transitionId);
	}

	public SparseIntVector postsetT(int transitionId) {
		return this.flowMatrixTP.getColumns().get(transitionId);
	}

	public SparseIntVector presetP_copy(int placeId) {
		return this.flowMatrixTP.getRow_copy(placeId);
	}

	public SparseIntVector presetP_copy_cached(int placeId) {
		return this.flowMatrixTP.getRow_copy_cached(placeId);
	}

	public Optional<SparseIntVector> presetP_copy(int placeId, int minSizeInclusive, int maxSizeInclusive) {
		return this.flowMatrixTP.getRow_copy(placeId, minSizeInclusive, maxSizeInclusive);
	}

	public SparseIntVector postsetP_copy(int placeId) {
		return this.flowMatrixPT.getRow_copy(placeId);
	}

	public SparseIntVector postsetP_copy_cached(int placeId) {
		return this.flowMatrixPT.getRow_copy_cached(placeId);
	}

	public Optional<SparseIntVector> postsetP_copy(int placeId, int minSizeInclusive, int maxSizeInclusive) {
		return this.flowMatrixPT.getRow_copy(placeId, minSizeInclusive, maxSizeInclusive);
	}

	public int weightPT(int placeId, int transitionId) {
		return this.flowMatrixPT.get(placeId, transitionId);
	}

	public int weightTP(int transitionId, int placeId) {
		return this.flowMatrixTP.get(placeId, transitionId);
	}

	public boolean firable(SparseIntVector marking, int transitionId) {
		return marking.greaterEquals(this.presetT(transitionId));
	}

	/**
	 * Return the successor marking, if firable, empty otherwise
	 */
	public Optional<SparseIntVector> fire(SparseIntVector marking, int transitionId) {
		SparseIntVector t_pre = this.presetT(transitionId);
		if (marking.greaterEquals(t_pre)) {
			SparseIntVector afterConsume = SparseIntVector.weightedSum(1, marking, -1, t_pre);
			SparseIntVector nextMarking = SparseIntVector.weightedSum(1, afterConsume, 1, this.postsetT(transitionId));
			return Optional.of(nextMarking);
		} else {
			return Optional.empty();
		}
	}
}
