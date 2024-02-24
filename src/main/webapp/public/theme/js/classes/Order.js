export class Order {
    constructor() {
        this.id = null;
        this.restaurantTable = null;
        this.restaurant = null;
        this.orderTime = null;
        this.orderedItems = [];
        this.paymentMethod = null;
        this.totalAmount = null;
        this.paid = false;
        this.forTakeAway = false;
        this.billRequested = false;
        this.isResolved = false;
        this.waiterCalled = false;
        this.orderNumber = null;
    }
}