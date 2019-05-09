public class CustContinuation extends Continuation {

    public CustContinuation(ContinuationScope scope, Runnable target) {
        super(scope, target);
    }

    public CustContinuation(ContinuationScope scope, int stackSize, Runnable target) {
        super(scope, stackSize, target);
    }

    @Override
    protected void onContinue() {
        super.onContinue();
//        run();
    }
}
