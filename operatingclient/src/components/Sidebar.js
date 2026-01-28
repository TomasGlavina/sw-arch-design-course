import { Link } from "react-router-dom";
import "./Sidebar.css";

function Sidebar() {
    return (
        <>
            <div className="Sidebar">
                <div className="sidebar-brand">
                    Fleet Console
                </div>
                <div className="sidebar-nav">
                    <button className="page_selection">
                        <Link to={"/drones"} className="nav-link">Drones</Link>
                    </button>
                    <button className="page_selection">
                        <Link to={"/deliveries"} className="nav-link">Deliveries</Link>
                    </button>
                </div>
            </div>
        </>
    );
}

export default Sidebar;
