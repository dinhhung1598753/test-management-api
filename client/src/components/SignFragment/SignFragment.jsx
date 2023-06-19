import React from "react";
import { FaGoogle } from "react-icons/fa";
import { TfiFacebook } from "react-icons/tfi";
import studyImage from "../../assets/images/self-learning.jpg";
import "./SignFragment.scss";
const SignFragment = ({
  header,
  socialText,
  endText,
  signText,
  children,
  href,
}) => {
  return (
    <div className="login">
      <div className="img-study">
        <img src={studyImage} alt="study img" />
      </div>
      <div className="login-form">
        <h1>{header}</h1>
        <div className="header-login-content">Access to our dashboard</div>
        {children}
        <div className="login-or">
          <span className="or-line"></span>
          <span className="or">OR</span>
        </div>
        <div className="social-login">
          <span>{socialText}</span>
          <a href="/facebook" className="facebook">
            <TfiFacebook size={16} />
          </a>
          <a href="/google" className="google">
            <FaGoogle size={16} />
          </a>
        </div>
        <div className="dont-have">
          {endText} <a href={href}>{signText}</a>
        </div>
      </div>
    </div>
  );
};
export default SignFragment;
