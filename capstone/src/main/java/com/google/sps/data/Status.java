enum Status 
{ 
  STRUGGLING(0), OKAY(1), GOOD(2);

  Status(int value) {
    this.value = value;
  } 

  private int value;

  public int getValue() {
    return value;
  }
}