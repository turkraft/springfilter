package com.turkraft.springfilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.annotation.Id;

public class TestEntity {

  @Id
  private String id;

  private String string;

  private List<Integer> integers;

  private int integer;

  private NestedTestEntity nested;

  private Map<String, String> metadata;

  private Map<String, Integer> counters;

  private Map<String, NestedTestEntity> nestedByName;

  private Map<String, Map<String, String>> nestedMaps;

  private Map<String, UUID> uuidByName;

  private Map rawMap;

  private List<UUID> uuidList;

  private Map<String, List<String>> tags;

  private Map<String, List<Integer>> intLists;

  private Map<String, UUID[]> uuidArrays;

  private Map<String, List<UUID>> uuidListByName;

  private Map<String, Status> statuses;

  private Map<String, Boolean> flags;

  private HashMap<String, String> hashMapWebsites;

  private Map<String, ?> wildcardMap;

  public enum Status {
    ACTIVE, INACTIVE
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public List<Integer> getIntegers() {
    return integers;
  }

  public void setIntegers(List<Integer> integers) {
    this.integers = integers;
  }

  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

  public NestedTestEntity getNested() {
    return nested;
  }

  public void setNested(NestedTestEntity nested) {
    this.nested = nested;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  public Map<String, Integer> getCounters() {
    return counters;
  }

  public void setCounters(Map<String, Integer> counters) {
    this.counters = counters;
  }

  public Map<String, NestedTestEntity> getNestedByName() {
    return nestedByName;
  }

  public void setNestedByName(Map<String, NestedTestEntity> nestedByName) {
    this.nestedByName = nestedByName;
  }

  public Map<String, Map<String, String>> getNestedMaps() {
    return nestedMaps;
  }

  public void setNestedMaps(Map<String, Map<String, String>> nestedMaps) {
    this.nestedMaps = nestedMaps;
  }

  public Map<String, UUID> getUuidByName() {
    return uuidByName;
  }

  public void setUuidByName(Map<String, UUID> uuidByName) {
    this.uuidByName = uuidByName;
  }

  public Map getRawMap() {
    return rawMap;
  }

  public void setRawMap(Map rawMap) {
    this.rawMap = rawMap;
  }

  public List<UUID> getUuidList() {
    return uuidList;
  }

  public void setUuidList(List<UUID> uuidList) {
    this.uuidList = uuidList;
  }

  public Map<String, List<String>> getTags() {
    return tags;
  }

  public void setTags(Map<String, List<String>> tags) {
    this.tags = tags;
  }

  public Map<String, List<Integer>> getIntLists() {
    return intLists;
  }

  public void setIntLists(Map<String, List<Integer>> intLists) {
    this.intLists = intLists;
  }

  public Map<String, UUID[]> getUuidArrays() {
    return uuidArrays;
  }

  public void setUuidArrays(Map<String, UUID[]> uuidArrays) {
    this.uuidArrays = uuidArrays;
  }

  public Map<String, List<UUID>> getUuidListByName() {
    return uuidListByName;
  }

  public void setUuidListByName(Map<String, List<UUID>> uuidListByName) {
    this.uuidListByName = uuidListByName;
  }

  public Map<String, Status> getStatuses() {
    return statuses;
  }

  public void setStatuses(Map<String, Status> statuses) {
    this.statuses = statuses;
  }

  public Map<String, Boolean> getFlags() {
    return flags;
  }

  public void setFlags(Map<String, Boolean> flags) {
    this.flags = flags;
  }

  public HashMap<String, String> getHashMapWebsites() {
    return hashMapWebsites;
  }

  public void setHashMapWebsites(HashMap<String, String> hashMapWebsites) {
    this.hashMapWebsites = hashMapWebsites;
  }

  public Map<String, ?> getWildcardMap() {
    return wildcardMap;
  }

  public void setWildcardMap(Map<String, ?> wildcardMap) {
    this.wildcardMap = wildcardMap;
  }

}
